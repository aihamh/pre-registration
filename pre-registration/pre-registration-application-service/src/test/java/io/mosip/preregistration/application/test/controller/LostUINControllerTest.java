package io.mosip.preregistration.application.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.preregistration.application.controller.LostUINController;
import io.mosip.preregistration.application.dto.ApplicationRequestDTO;
import io.mosip.preregistration.application.dto.ApplicationResponseDTO;
import io.mosip.preregistration.application.dto.DeleteApplicationDTO;
import io.mosip.preregistration.application.service.ApplicationServiceIntf;
import io.mosip.preregistration.core.code.BookingTypeCodes;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.util.RequestValidator;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LostUINController.class)
@Import(LostUINController.class)
@WithMockUser(username = "individual", authorities = { "INDIVIDUAL", "REGISTRATION_OFFICER" })
public class LostUINControllerTest {

	@Mock
	ApplicationServiceIntf applicationService;

	@Mock
	private RequestValidator requestValidator;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private LostUINController lostuinController;

	private MockMvc mockmvc;

	@Value("${mosip.id.preregistration.lostuin.create}")
	private String createId;

	@Value("${mosip.id.preregistration.lostuin.delete}")
	private String deleteId;

	@Before
	public void setup() {
		mockmvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void addLostUinApplicationTest() throws Exception {
		MainResponseDTO<ApplicationResponseDTO> mainResponseDto = new MainResponseDTO<ApplicationResponseDTO>();
		ApplicationResponseDTO applicationDto = new ApplicationResponseDTO();
		MainRequestDTO<ApplicationRequestDTO> mainRequestDto = new MainRequestDTO<ApplicationRequestDTO>();
		ApplicationRequestDTO applicationRequestDto = new ApplicationRequestDTO();

		applicationRequestDto.setLangCode("eng");
		mainRequestDto.setVersion("1.0");
		mainRequestDto.setId(createId);
		mainRequestDto.setRequest(applicationRequestDto);

		applicationDto.setApplicationId("123456789");
		applicationDto.setApplicationStatusCode("SUBMITTED");
		applicationDto.setBookingStatusCode("Pending_Appointment");
		applicationDto.setBookingType("LOST_FORGOTTEN_UIN");
		mainResponseDto.setResponse(applicationDto);
		mainResponseDto.setId(createId);
		mainResponseDto.setResponsetime(DateTime.now().toString());
		Mockito.when(applicationService.addLostOrUpdateApplication(mainRequestDto,
				BookingTypeCodes.LOST_FORGOTTEN_UIN.toString())).thenReturn(mainResponseDto);
		mockmvc.perform(post("/applications/lostuin").contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"id\":\""
				+ createId
				+ "\",\"request\":{\"lang_code\":\"eng\"},\"version\":\"1.0\",\"requesttime\":\"2021-12-29T18:47:43.190Z\"}"))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteLostUinApplicationTest() throws Exception {
		String applicationId = "123456789";
		String bookingType = BookingTypeCodes.LOST_FORGOTTEN_UIN.toString();
		MainResponseDTO<DeleteApplicationDTO> response = new MainResponseDTO<DeleteApplicationDTO>();
		response.setId(deleteId);
		Mockito.when(applicationService.deleteLostOrUpdateApplication(applicationId, bookingType)).thenReturn(response);
		RequestBuilder request = MockMvcRequestBuilders.delete("/applications/lostuin/{applicationId}", applicationId)
				.param("applicationId", applicationId).accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_UTF8);
		mockmvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
	}

}
