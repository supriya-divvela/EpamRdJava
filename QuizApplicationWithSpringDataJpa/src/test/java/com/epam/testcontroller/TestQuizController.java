package com.epam.testcontroller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.epam.controller.QuizController;
import com.epam.dto.QuizDto;
import com.epam.exception.DuplicateQuizNumberException;
import com.epam.model.Question;
import com.epam.model.Quiz;
import com.epam.service.QuizService;

@WebMvcTest(QuizController.class)
class TestQuizController {
	@MockBean
	private QuizService quizService;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void testCreateQuiz() throws Exception {
		mockMvc.perform(get("/createquiz")).andExpect(status().isOk()).andExpect(view().name("createquiz"));
	}

	@Test
	void testDeleteQuiz() throws Exception {
		when(quizService.removeQuiz(2)).thenReturn(2);
		List<Quiz> quizes=new ArrayList<>();
		when(quizService.displayAvaliableQuizes()).thenReturn(quizes);
		mockMvc.perform(get("/deletequiz").param("quizNo", "2")).andExpect(status().isOk()).andExpect(view().name("quizpage")).andExpect(model().attribute("listOfQuizs",quizes)).andExpect(model().attribute("deletequizmessage","Quiz Deleted Succesfully..."));
	}

	@Test
	void testQuizPage() throws Exception {
		when(quizService.removeQuiz(2)).thenReturn(2);
		List<Quiz> quizes=new ArrayList<>();
		when(quizService.displayAvaliableQuizes()).thenReturn(quizes);
		mockMvc.perform(get("/quizpage").param("quizNo", "2")).andExpect(status().isOk()).andExpect(view().name("quizpage")).andExpect(model().attribute("listOfQuizs",quizes)).andExpect(model().attribute("emptyquizlibrary","Empty quiz library..."));
	
	}

	@Test
	void testQuizPageWithException() throws Exception {
		when(quizService.removeQuiz(2)).thenReturn(2);
		List<Quiz> quizes=new ArrayList<>();
		quizes.add(new Quiz());
		when(quizService.displayAvaliableQuizes()).thenReturn(quizes);
		mockMvc.perform(get("/quizpage").param("quizNo", "2")).andExpect(status().isOk()).andExpect(view().name("quizpage")).andExpect(model().attribute("listOfQuizs",quizes));
	
	}

	@Test
	void testAddQuiz() throws Exception {
		QuizDto quizDto = new QuizDto();
		when(quizService.addQuiz(quizDto)).thenReturn(2);
		List<Quiz> quizes = new ArrayList<>();
		when(quizService.displayAvaliableQuizes()).thenReturn(quizes);
		when(quizService.createQuiz(2, "java", Arrays.asList(1, 2, 3))).thenReturn(quizDto);
		mockMvc.perform(post("/addquiz").param("quizNo", "2").param("title", "java").param("listOfQuestions", "1,2,3"))
				.andExpect(status().isOk()).andExpect(view().name("createquiz"))
				.andExpect(model().attribute("createquiz", "quiz created succesfull.."));
	}

	@Test
	void testAddQuizWithException() throws Exception {
		QuizDto quizDto = new QuizDto();
		when(quizService.addQuiz(quizDto)).thenThrow(DuplicateQuizNumberException.class);
		List<Quiz> quizes = new ArrayList<>();
		when(quizService.displayAvaliableQuizes()).thenReturn(quizes);
		when(quizService.createQuiz(2, "java", Arrays.asList(1, 2, 3))).thenReturn(quizDto);
		mockMvc.perform(post("/addquiz").param("quizNo", "2").param("title", "java").param("listOfQuestions", "1,2,3"))
				.andExpect(status().isOk()).andExpect(view().name("createquiz"))
				.andExpect(model().attribute("duplicatequiz", "duplicate quiz number found"));
	}

	@Test
	void testUpdateQuiz() throws Exception {
		Quiz quiz = new Quiz();
		Question question = new Question();
		question.setQNo(2);
		quiz.setListOfQuestions(Arrays.asList(question));
		when(quizService.getQuiz(2)).thenReturn(quiz);
		mockMvc.perform(post("/updatequiz").param("quizNo", "2")).andExpect(status().isOk())
				.andExpect(view().name("updatequiz")).andExpect(model().attribute("questionnumbers", "2"));
	}

	@Test
	void testUpdatingQuiz() throws Exception {
		List<Quiz> quizes = new ArrayList<>();
		QuizDto quizDto = new QuizDto();
		Quiz quiz = new Quiz();
		quizDto.setQuizNo(2);
		quizes.add(quiz);
		when(quizService.createQuiz(2, "java", Arrays.asList(1, 2, 3, 4))).thenReturn(quizDto);
		when(quizService.displayAvaliableQuizes()).thenReturn(quizes);
		doNothing().when(quizService).updateQuiz(2, quizDto);
		mockMvc.perform(post("/updatequizwithdetails").param("quizNo", "2").param("title", "java")
				.param("listOfQuestions", "1,2,3,4")).andExpect(status().isOk()).andExpect(view().name("quizpage"))
				.andExpectAll(model().attribute("listOfQuizs", quizes))
				.andExpect(model().attribute("updatequiz", "2 Question updated succesfully..."));
	}
}
