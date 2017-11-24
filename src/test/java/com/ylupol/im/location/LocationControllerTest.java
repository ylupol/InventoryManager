package com.ylupol.im.location;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private WebApplicationContext context;

    @Rule
    public JUnitRestDocumentation restDocumentation =
        new JUnitRestDocumentation("build/generated-snippets");

    private RestDocumentationResultHandler document;

    @Before
    public void setUp() {
        document = document("{class-name}/{method-name}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()));
        mockMvc =
            MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation).uris()
                    .withScheme("http")
                    .withHost("example.com")
                    .withPort(8080))
                .alwaysDo(document)
                .build();
    }

    @After
    public void tearDown() {
        locationRepository.deleteAll();
    }

    @Test
    public void testGetById() throws Exception {
        Location location = Location.builder().name("Warehouse")
            .description("Warehouse description").build();
        locationRepository.save(location);

        mockMvc.perform(get("/v1/locations/{id}", location.getId())
            .accept(APPLICATION_JSON))
            .andExpect(jsonPath("id", is(location.getId())))
            .andExpect(jsonPath("name", is("Warehouse")))
            .andExpect(jsonPath("description", is("Warehouse description")))
            .andDo(document.document(
                pathParameters(parameterWithName("id").description("Location id")),
                getReponseFields()));
    }

    @Test
    public void testGetByUnknownId() throws Exception {
        mockMvc
            .perform(get("/v1/locations/{id}", 123).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetByName() throws Exception {
        locationRepository.save(Location.builder().name("apartment").description("city").build());
        locationRepository.save(Location.builder().name("house").description("suburb").build());

        mockMvc
            .perform(get("/v1/locations/name/{name}", "house")
                .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name", is("house")))
            .andDo(document.document(
                pathParameters(parameterWithName("name").description("Location name")),
                getReponseFields()));
    }

    @Test
    public void testGetByUnknownName() throws Exception {
        mockMvc
            .perform(get("/v1/locations/name/{name}", "unknown name")
                .accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAll() throws Exception {
        locationRepository.save(Location.builder().name("apartment").description("city").build());
        locationRepository.save(Location.builder().name("house").description("suburb").build());

        mockMvc
            .perform(get("/v1/locations").accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andDo(
                document.document(responseFields(
                    fieldWithPath("[].id").description("Location id"),
                    fieldWithPath("[].name").description("Location name"),
                    fieldWithPath("[].description").description("Location description"))));
    }

    @Test
    public void testCreate() throws Exception {
        LocationInputDto location =
            LocationInputDto.builder().name("Warehouse").description("Description").build();

        ConstrainedFields fields = new ConstrainedFields(LocationInputDto.class);

        mockMvc
            .perform(
                post("/v1/locations")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(location)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", greaterThan(0)))
            .andExpect(jsonPath("$.name", is("Warehouse")))
            .andExpect(jsonPath("$.description", is("Description")))
            .andDo(document.document(
                requestFields(
                    fields.withPath("name").description("Location name"),
                    fields.withPath("description").description("Location description")),
                responseFields(
                    fieldWithPath("id").description("Created location id"),
                    fieldWithPath("name").description("Location name"),
                    fieldWithPath("description").description("Location description"))));
    }

    @Test
    public void testCreateWithExistingName() throws Exception {
        Location location = Location.builder().name("Warehouse").description("Description").build();
        locationRepository.save(location);

        mockMvc
            .perform(
                post("/v1/locations")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(location)))
            .andExpect(status().isConflict())
            .andExpect(status().reason("Location with such name exists already"));
    }

    @Test
    public void testDelete() throws Exception {
        Location location = Location.builder().name("Warehouse").description("Description").build();
        locationRepository.save(location);

        mockMvc
            .perform(delete("/v1/locations/{id}", location.getId()).accept(APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andDo(document.document(
                pathParameters(parameterWithName("id").description("Location id"))));
    }

    @Test
    public void testDeleteByUnknownId() throws Exception {
        mockMvc
            .perform(delete("/v1/locations/{id}", 123).accept(APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdate() throws Exception {
        Location location = Location.builder().name("Warehouse").description("Description").build();
        locationRepository.save(location);

        LocationInputDto locationInputDto =
            LocationInputDto.builder().name("Basement").description("Garage").build();

        ConstrainedFields fields = new ConstrainedFields(LocationInputDto.class);

        mockMvc
            .perform(
                put("/v1/locations/{id}", location.getId())
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(locationInputDto)))
            .andExpect(status().isOk())
            .andDo(document.document(
                pathParameters(
                    parameterWithName("id").description("Location id")),
                requestFields(
                    fields.withPath("name").description("Location name"),
                    fields.withPath("description").description("Location description"))));
    }

    @Test
    public void testUpdateByUnknownId() throws Exception {
        LocationInputDto locationInputDto =
            LocationInputDto.builder().name("Basement").description("Garage").build();

        mockMvc
            .perform(
                put("/v1/locations/{id}", 123)
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(locationInputDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateWithExistingName() throws Exception {
        Location location = Location.builder().name("Warehouse").description("Description").build();
        locationRepository.save(location);

        LocationInputDto locationInputDto =
            LocationInputDto.builder().name("Warehouse").description("Garage").build();

        mockMvc
            .perform(
                put("/v1/locations/{id}", location.getId())
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(locationInputDto)))
            .andExpect(status().isConflict())
            .andExpect(status().reason("Location with such name exists already"));
    }

    private ResponseFieldsSnippet getReponseFields() {
        return responseFields(
            fieldWithPath("id").description("Location id"),
            fieldWithPath("name").description("Location name"),
            fieldWithPath("description").description("Location description"));
    }

    /**
     * Add constrains to generated snippets. Also test/resources/org/springframework/restdocs/templates/request-fields.snippet
     * should be present to override Spring's default.
     *
     * @see <a href="https://www.tothepoint.company/blog/spring-rest-doc">Explanation in the
     * blog</a>
     */
    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                .collectionToDelimitedString(this.constraintDescriptions
                    .descriptionsForProperty(path), ". ")));
        }
    }
}
