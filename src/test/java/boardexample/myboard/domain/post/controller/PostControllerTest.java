package boardexample.myboard.domain.post.controller;

import boardexample.myboard.domain.member.Member;
import boardexample.myboard.domain.member.Role;
import boardexample.myboard.domain.member.repository.MemberRepository;
import boardexample.myboard.domain.post.Post;
import boardexample.myboard.domain.post.dto.PostInfoDto;
import boardexample.myboard.domain.post.dto.PostPagingDto;
import boardexample.myboard.domain.post.repository.PostRepository;
import boardexample.myboard.global.file.service.FileService;
import boardexample.myboard.global.jwt.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired EntityManager em;

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;
    final String USERNAME = "username1";

    private static  Member member;


    private void clear() {
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void signUpMember(){
        member = memberRepository.save(Member.builder().username(USERNAME).password("1234567890").name("USER1").nickName("??? ????????? ?????????1").role(Role.USER).age(22).build());
        clear();
    }

    private String getAccessToken(){
        return jwtService.createAccessToken(USERNAME);
    }


    private MockMultipartFile getMockUploadFile() throws IOException {
        //TODO : name??? ??????
        return new MockMultipartFile("uploadFile", "file.jpg", "image/jpg", new FileInputStream("C:/Users/user/Desktop/tistory/diary.jpg"));
    }

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????() throws Exception {
        //given
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "??????");
        map.add("content", "??????");


        //when
        mockMvc.perform(
                post("/post")
                        .header("Authorization", "Bearer "+ getAccessToken())
                .contentType(MediaType.MULTIPART_FORM_DATA).params(map))
                .andExpect(status().isCreated());


        //then
        Assertions.assertThat(postRepository.findAll().size()).isEqualTo(1);
    }

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????_????????????_?????????_??????() throws Exception {
        //given

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "??????");


        //when, then
        mockMvc.perform(
                        post("/post")
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isBadRequest());

        map = new LinkedMultiValueMap<>();
        map.add("content", "??????");
        mockMvc.perform(
                        post("/post")
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isBadRequest());

    }




    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_????????????_??????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(member);
        Post savePost = postRepository.save(post);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_TITLE = "??????";
        map.add("title", UPDATE_TITLE);

        //when
        mockMvc.perform(
                        put("/post/"+savePost.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))

                .andExpect(status().isOk());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITLE);
    }

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_????????????_??????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(member);
        Post savePost = postRepository.save(post);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "??????";
        map.add("content", UPDATE_CONTENT);

        //when
        mockMvc.perform(
                        put("/post/"+savePost.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
    }



    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????????????????_??????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(member);
        Post savePost = postRepository.save(post);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "??????";
        final String UPDATE_TITlE = "??????";
        map.add("title", UPDATE_TITlE);
        map.add("content", UPDATE_CONTENT);

        //when
        mockMvc.perform(
                        put("/post/"+savePost.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITlE);
    }

    /**
      ????????? ??????
     */
    @Test
    public void ?????????_??????_?????????????????????_??????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(member);
        Post savePost = postRepository.save(post);

        MockMultipartFile mockUploadFile = getMockUploadFile();


        //when

        MockMultipartHttpServletRequestBuilder requestBuilder = multipart("/post/" + savePost.getId());
        requestBuilder.with(request -> {
            request.setMethod(HttpMethod.PUT.name());
            return request;
        });

        mockMvc.perform(requestBuilder
                        .file(getMockUploadFile())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isOk());

        /*mockMvc.perform(multipart("/post/"+savePost.getId())

                                .file(getMockUploadFile())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                               )
                .andExpect(status().isOk());
*/

        //then
        String filePath = postRepository.findAll().get(0).getFilePath();
        Assertions.assertThat(filePath).isNotNull();
        Assertions.assertThat(new File(filePath).delete()).isTrue();

    }

    /**
     ????????? ??????
     */
    @Autowired private FileService fileService;
    @Test
    public void ?????????_??????_?????????????????????_??????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(member);
        String path = fileService.save(getMockUploadFile());
        post.updateFilePath(path);
        Post savePost = postRepository.save(post);

        Assertions.assertThat(postRepository.findAll().get(0).getFilePath()).isNotNull();


        MockMultipartFile mockUploadFile = getMockUploadFile();


        //when

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "??????";
        final String UPDATE_TITlE = "??????";
        map.add("title", UPDATE_TITlE);
        map.add("content", UPDATE_CONTENT);

        //when
        mockMvc.perform(
                        put("/post/"+savePost.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITlE);
        Assertions.assertThat(postRepository.findAll().get(0).getFilePath()).isNull();

    }



    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????_????????????() throws Exception {
        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").name("123213").nickName("123").age(22).role(Role.USER).build());
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(newMember);
        Post savePost = postRepository.save(post);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        final String UPDATE_CONTENT = "??????";
        final String UPDATE_TITlE = "??????";
        map.add("title", UPDATE_TITlE);
        map.add("content", UPDATE_CONTENT);

        //when
        mockMvc.perform(
                        put("/post/"+savePost.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .params(map))
                .andExpect(status().isForbidden());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo("???????????????");
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo("???????????????");
    }



    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(member);
        Post savePost = postRepository.save(post);

        //when
        mockMvc.perform(
                        delete("/post/"+savePost.getId())
                                .header("Authorization", "Bearer "+ getAccessToken())
                ).andExpect(status().isOk());


        //then
        Assertions.assertThat(postRepository.findAll().size()).isEqualTo(0);

    }

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????_??????_????????????() throws Exception {
        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").name("123213").nickName("123").age(22).role(Role.USER).build());
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(newMember);
        Post savePost = postRepository.save(post);

        //when
        mockMvc.perform(
                delete("/post/"+savePost.getId())
                        .header("Authorization", "Bearer "+ getAccessToken())
        ).andExpect(status().isForbidden());


        //then
        Assertions.assertThat(postRepository.findAll().size()).isEqualTo(1);

    }


    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????() throws Exception {

        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").name("123213").nickName("123").age(22).role(Role.USER).build());
        Post post = Post.builder().title("title").content("content").build();
        post.confirmWriter(newMember);
        Post savePost = postRepository.save(post);

        //when
        MvcResult result = mockMvc.perform(
                get("/post/" + savePost.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
        ).andExpect(status().isOk()).andReturn();

        PostInfoDto postInfoDto = objectMapper.readValue(result.getResponse().getContentAsString(), PostInfoDto.class);


        //then
        Assertions.assertThat(postInfoDto.getPostId()).isEqualTo(post.getId());
        Assertions.assertThat(postInfoDto.getContent()).isEqualTo(post.getContent());
        Assertions.assertThat(postInfoDto.getTitle()).isEqualTo(post.getTitle());


    }


    @Value("${spring.data.web.pageable.default-page-size}")
    private int pageCount;

    /**
     * ????????? ??????
     */
    @Test
    public void ?????????_??????() throws Exception {

        //given
        Member newMember = memberRepository.save(Member.builder().username("newMEmber1123").password("!23123124421").name("123213").nickName("123").age(22).role(Role.USER).build());




        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("title"+ i).content("content"+i).build();
            post.confirmWriter(newMember);
            postRepository.save(post);
        }

        clear();



        //when
        MvcResult result = mockMvc.perform(
                get("/post")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
        ).andExpect(status().isOk()).andReturn();

        //then
        PostPagingDto postList = objectMapper.readValue(result.getResponse().getContentAsString(), PostPagingDto.class);

        assertThat(postList.getTotalElementCount()).isEqualTo(POST_COUNT);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(pageCount);
        assertThat(postList.getSimpleLectureDtoList().get(0).getContent()).isEqualTo("content50");

    }
}