package com.swagger.doc.test;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.Placement;
import org.asciidoctor.SafeMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.github.robwin.markup.builder.MarkupLanguage;
import io.github.swagger2markup.Swagger2MarkupConverter;
import springfox.documentation.staticdocs.Swagger2MarkupResultHandler;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-mvc.xml", "classpath:spring-component.xml" })
public class Swagger2MarkupTest {
	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void convertSwaggerToAsciiDoc() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
				.andDo(Swagger2MarkupResultHandler.outputDirectory("src/docs/asciidoc/generated").build())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void convertSwaggerToMarkdown() throws Exception {

		this.mockMvc.perform(MockMvcRequestBuilders.get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
				.andDo(Swagger2MarkupResultHandler.outputDirectory("src/docs/markdown/generated")
						.withMarkupLanguage(MarkupLanguage.MARKDOWN).build())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void convertSwaggerToAsciiDoc2Html() throws Exception {
		MvcResult mvcResult = this.mockMvc
				.perform(MockMvcRequestBuilders.get("/v2/api-docs").accept("application/json;charset=utf-8"))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		// 文档输出目录
		String outputDirectory = "src/docs/restful/generated/index";
		Path outputDirectoryPath = Paths.get(outputDirectory);
		MockHttpServletResponse response = mvcResult.getResponse();
		String swaggerJson = response.getContentAsString();
		Swagger2MarkupConverter.from(swaggerJson).build().toFile(outputDirectoryPath);// .toFolder(outputDirectoryPath);
		
		//index.adoc-->index.html
		Asciidoctor asciidoctor = Asciidoctor.Factory.create();
		Attributes attributes = new Attributes();
		attributes.setCopyCss(true);
		attributes.setLinkCss(false);
		attributes.setSectNumLevels(3);
		attributes.setAnchors(true);
		attributes.setSectionNumbers(true);
		attributes.setHardbreaks(true);
		attributes.setTableOfContents(Placement.LEFT);
		attributes.setAttribute("generated", "generated");
		OptionsBuilder optionsBuilder = OptionsBuilder.options().backend("html5").docType("book").eruby("")
				.inPlace(true).safe(SafeMode.UNSAFE).attributes(attributes);
		String asciiInputFile = "src/docs/restful/generated/index.adoc";
		asciidoctor.convertFile(new File(asciiInputFile), optionsBuilder.get());

	}
}
