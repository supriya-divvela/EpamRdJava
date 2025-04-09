package com.epam.testcontroller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.epam.controller.QuestionController;
import com.epam.dto.QuestionDto;
import com.epam.exception.DuplicateQuestionNumberException;
import com.epam.exception.EmptyQuestionLibraryException;
import com.epam.exception.QuestionNotFoundException;
import com.epam.model.Question;
import com.epam.service.QuestionService;

@WebMvcTest(QuestionController.class)
class TestQuestionController {
	@MockBean
	private QuestionService questionService;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void testCreateQuestionPage() throws Exception {
		mockMvc.perform(get("/createquestion")).andExpect(status().isOk()).andExpect(view().name("createquestion"));
	}

	@Test
	void testQuestionPage() throws Exception {
		List<Question> questions = new ArrayList<>();
		when(questionService.displayQuestions()).thenReturn(questions);
		mockMvc.perform(get("/questionpage")).andExpect(status().isOk()).andExpect(view().name("questionpage"))
				.andExpect(model().attribute("listOfQuestions", questions));

	}

	@Test
	void testQuestionPageWithException() throws Exception {
		when(questionService.displayQuestions()).thenThrow(EmptyQuestionLibraryException.class);
		mockMvc.perform(get("/questionpage")).andExpect(status().isOk()).andExpect(view().name("questionpage")).andExpect(model().attribute("emptyquestionlibrary","Empty Question Library.."));
		
	}

	@Test
	void testCreateQuestion() throws Exception {
		QuestionDto questionDto = new QuestionDto();
		when(questionService.addQuestion(questionDto)).thenReturn(2);
		mockMvc.perform(post("/addquestion").param("qNo", "2").param("marks", "2")
				.param("title", questionDto.getTitle()).param("optionsList", "1,2,3,4").param("answer", "2")
				.param("difficulty", "hard").param("taggingTopic", "java")).andExpect(status().isOk())
				.andExpect(view().name("createquestion"))
				.andExpect(model().attribute("createquestion", "question created succesfull.."));
	}

	@Test
	void testCreateQuestionWithException() throws Exception {
		when(questionService.addQuestion(Mockito.any(QuestionDto.class))).thenThrow(DuplicateQuestionNumberException.class);
		mockMvc.perform(post("/addquestion").param("qNo", "2").param("marks", "2")
				.param("title", "hello").param("optionsList", "1,2,3,4").param("answer", "2")
				.param("difficulty", "hard").param("taggingTopic", "java")).andExpect(status().isOk())
				.andExpect(view().name("createquestion"))
				.andExpect(model().attribute("duplicatequestion", "duplicate question number found"));
	}

	@Test
	void testDeleteQuestion() throws Exception {
		when(questionService.removeQuestion(2)).thenReturn(2);
		List<Question> questions=new ArrayList<>();
		when(questionService.displayQuestions()).thenReturn(questions);
		mockMvc.perform(get("/deletequestion").param("qNo", "2")).andExpect(status().isOk())
				.andExpect(view().name("questionpage"))
				.andExpect(model().attribute("listOfQuestions", questions))
				.andExpect(model().attribute("deletequestionmessage", "Question Deleted Succesfully..."));
	}

	@Test
	void testDeleteQuestionWithException() throws Exception {
		when(questionService.removeQuestion(2)).thenThrow(QuestionNotFoundException.class);
		mockMvc.perform(get("/deletequestion").param("qNo", "2")).andExpect(status().isOk())
				.andExpect(view().name("questionpage"))
				.andExpect(model().attribute("emptyquestionlibrary", "Empty question library..."));
	}

	@Test
	void testUpdateQuestionPage() throws Exception {
		Question question = new Question();
		List<String> options=new ArrayList<>();
		options.add("hello");
		Set<Integer> answers=new TreeSet<>();
		answers.add(1);
		question.setOptions(options);
		question.setAnswers(answers);
		question.setQNo(2);
		when(questionService.getQuestion(2)).thenReturn(question);
		mockMvc.perform(get("/updatequestion").param("qNo", "2")).andExpect(status().isOk())
				.andExpect(view().name("updatequestion")).andExpect(model().attribute("question", question))
				.andExpect(model().attribute("options","hello"))
				.andExpect(model().attribute("answers","1"));
	}
	@Test
	void testUpdatingQuestion() throws Exception {
		QuestionDto questionDto = new QuestionDto();
		List<String> options=new ArrayList<>();
		options.add("hello");
		Set<Integer> answers=new TreeSet<>();
		answers.add(1);
		questionDto.setOptions(options);
		questionDto.setAnswers(answers);
		questionDto.setQNo(2);
		List<Question> questions=new ArrayList<>();
		questions.add(new Question());
		when(questionService.displayQuestions()).thenReturn(questions);
		doNothing().when(questionService).updateQuestion(2,questionDto);
		mockMvc.perform(get("/updatequestionwithdetails").param("qNo", "2")).andExpect(status().isOk())
				.andExpect(view().name("questionpage"))
				.andExpect(model().attribute("listOfQuestions",questions))
				.andExpect(model().attribute("updatequestion","2 Question updated succesfully..."));
	}
}
