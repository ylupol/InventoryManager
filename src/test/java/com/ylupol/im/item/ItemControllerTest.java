package com.ylupol.im.item;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylupol.im.location.Location;
import com.ylupol.im.location.LocationRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

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
        itemRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    public void testGetById() throws Exception {
        Location location = locationRepository.save(Location.builder()
            .name("basement")
            .description("suburb")
            .build());

        Item box = itemRepository.save(Item.builder().name("box").location(location).build());

        mockMvc.perform(get("/v1/items/{id}", box.getId())
            .accept(APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(location.getId())))
            .andExpect(jsonPath("$.name", is("box")))
            .andExpect(jsonPath("$.location", is("basement")))
            .andDo(document.document(
                pathParameters(parameterWithName("id").description("Item id")),
                getReponseFields()));
    }

    private ResponseFieldsSnippet getReponseFields() {
        return responseFields(
            fieldWithPath("id").description("Item id"),
            fieldWithPath("name").description("Item name"),
            fieldWithPath("location").description("Location"));
    }

}