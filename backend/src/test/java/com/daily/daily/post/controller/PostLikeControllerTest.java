package com.daily.daily.post.controller;

import com.daily.daily.auth.jwt.JwtAuthorizationFilter;
import com.daily.daily.auth.jwt.JwtUtil;
import com.daily.daily.post.service.PostLikeFacade;
import com.daily.daily.post.service.PostLikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static com.daily.daily.post.fixture.PostFixture.POST_ID;
import static com.daily.daily.testutil.document.RestDocsUtil.document;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PostLikeController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtil.class),
})
@AutoConfigureRestDocs
class PostLikeControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostLikeService postLikeService;

    @MockBean
    PostLikeFacade postLikeFacade;

    @Nested
    @DisplayName("increaseLikeCount() - 좋아요 증가 메서드 테스트")
    class increaseLikeCount {

        @Test
        @DisplayName("성공적으로 좋아요를 증가시켰을 경우 응답 결과를 확인한다.")
        @WithMockUser
        void test1() throws Exception {
            //when
            ResultActions perform = mockMvc.perform(post("/api/posts/{postId}/likes", POST_ID)
                    .with(csrf().asHeader())
                    .contentType(MediaType.ALL)
            );

            //then
            perform.andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.successful").value(true))
                    .andExpect(jsonPath("$.statusCode").value(200));

            //restdocs
            perform.andDo(document("좋아요 증가",
                    pathParameters(
                            parameterWithName("postId").description("좋아요할 게시글 id")
                    )
            ));
        }
    }
    
    @Nested
    @DisplayName("decreasedLikeCount() - 좋아요 취소 메서드 테스트")
    class decreaseLikeCount {
        @Test
        @DisplayName("성공적으로 좋아요를 취소시켰을 경우 응답 결과를 확인한다.")
        @WithMockUser
        void test1() throws Exception {
            //when
            ResultActions perform = mockMvc.perform(delete("/api/posts/{postId}/likes", POST_ID)
                    .with(csrf().asHeader())
                    .contentType(MediaType.ALL)
            );

            //then
            perform.andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.successful").value(true))
                    .andExpect(jsonPath("$.statusCode").value(200));

            //restdocs
            perform.andDo(document("좋아요 취소",
                    pathParameters(
                            parameterWithName("postId").description("좋아요를 취소할 게시글 id")
                    )
            ));
        }
    }

    @Nested
    @DisplayName("getLikeStatus() - 좋아요 상태 조회 테스트")
    class getLikeStatus {
        @Test
        @DisplayName("성공적으로 좋아요를 조회했을 때 응답결과를 확인한다.")
        @WithMockUser
        void test1() throws Exception {
            //given
            Map<Long, Boolean> likeStatus = Map.of(1L, true, 2L, true, 3L, false, 4L, false, 5L, true);
            BDDMockito.given(postLikeService.getLikeStatus(any(), any())).willReturn(likeStatus);

            //when
            ResultActions perform = mockMvc.perform(get("/api/posts/likes")
                    .queryParam("postIds", "1", "2", "3", "4", "5")
                    .with(csrf().asHeader())
                    .contentType(MediaType.ALL)
            );

            //then
            perform.andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$['1']").value(true))
                    .andExpect(jsonPath("$['2']").value(true))
                    .andExpect(jsonPath("$['3']").value(false))
                    .andExpect(jsonPath("$['4']").value(false))
                    .andExpect(jsonPath("$['5']").value(true));

            //restdocs
            perform.andDo(document("좋아요 눌렀는지 조회",
                    queryParameters(
                            parameterWithName("postIds").description("눌렀는지 여부를 확인할 게시글 id들")
                    ),
                    relaxedResponseFields(
                            fieldWithPath("*").description("게시글 ID와 눌렀는지 여부 \n (눌렀을시 TRUE / 아닐시 FALSE)")
                    )
            ));
        }
    }
}