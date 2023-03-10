package boardexample.myboard.domain.member.service;

import boardexample.myboard.domain.member.Member;
import boardexample.myboard.domain.member.Role;
import boardexample.myboard.domain.member.dto.MemberInfoDto;
import boardexample.myboard.domain.member.dto.MemberSignUpDto;
import boardexample.myboard.domain.member.dto.MemberUpdateDto;
import boardexample.myboard.domain.member.exception.MemberException;
import boardexample.myboard.domain.member.exception.MemberExceptionType;
import boardexample.myboard.domain.member.repository.MemberRepository;
import boardexample.myboard.global.util.security.SecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {


    @Autowired EntityManager em;
    @Autowired
    MemberRepository memberRepository;

    @Autowired MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    String PASSWORD = "password";

    private void clear(){
        em.flush();
        em.clear();
    }

    private MemberSignUpDto makeMemberSignUpDto() {
        return new MemberSignUpDto("username",PASSWORD,"name","nickNAme",22);
    }

    private MemberSignUpDto setMember() throws Exception {
        MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username(memberSignUpDto.username())
                .password(memberSignUpDto.password())
                .roles(Role.USER.name())
                .build(),
                null, null));

        SecurityContextHolder.setContext(emptyContext);
        return memberSignUpDto;
    }


    @AfterEach
    public void removeMember(){
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }







    /**
     * ????????????
     *    ???????????? ??? ?????????, ????????????, ??????, ??????, ????????? ???????????? ????????? ??????
     *    ?????? ???????????? ???????????? ????????? ??????
     *    ???????????? ??? ????????? ROLE ??? USER
     *
     *
     */
    @Test
    public void ????????????_??????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();

        //when
        memberService.signUp(memberSignUpDto);
        clear();

        //then
        Member member = memberRepository.findByUsername(memberSignUpDto.username()).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        assertThat(member.getId()).isNotNull();
        assertThat(member.getUsername()).isEqualTo(memberSignUpDto.username());
        assertThat(member.getName()).isEqualTo(memberSignUpDto.name());
        assertThat(member.getNickName()).isEqualTo(memberSignUpDto.nickName());
        assertThat(member.getAge()).isEqualTo(memberSignUpDto.age());
        assertThat(member.getRole()).isSameAs(Role.USER);

    }

    @Test
    public void ????????????_??????_??????_???????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();
        memberService.signUp(memberSignUpDto);
        clear();

        //when, then
        assertThat(assertThrows(MemberException.class, () -> memberService.signUp(memberSignUpDto)).getExceptionType()).isEqualTo(MemberExceptionType.ALREADY_EXIST_USERNAME);

    }


    @Test
    public void ????????????_??????_??????????????????_??????????????????_??????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto1 = new MemberSignUpDto(null,passwordEncoder.encode(PASSWORD),"name","nickNAme",22);
        MemberSignUpDto memberSignUpDto2 = new MemberSignUpDto("username",null,"name","nickNAme",22);
        MemberSignUpDto memberSignUpDto3 = new MemberSignUpDto("username",passwordEncoder.encode(PASSWORD),null,"nickNAme",22);
        MemberSignUpDto memberSignUpDto4 = new MemberSignUpDto("username",passwordEncoder.encode(PASSWORD),"name",null,22);
        MemberSignUpDto memberSignUpDto5 = new MemberSignUpDto("username",passwordEncoder.encode(PASSWORD),"name","nickNAme",null);


        //when, then

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto1));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto2));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto3));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto4));

        assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto5));
    }


    /**
     * ??????????????????
     * ??????????????? ?????? ?????? ????????? ??????????????? ?????? -> ???????????? ????????? ????????? ???????????????
     * ???????????? ?????? ?????????
     * ???????????? ???????????????, ?????? ??????????????? ???????????????, ????????? ???????????? ?????? ??? ??????
     * ???????????? ??????????????? ?????? ??????????????? ?????? ??? ??????
     *
     * ??????????????? ?????? ??????,??????,?????? ?????? ?????????, 3?????? ???????????? ?????? ?????? ??????, ???,????????? ???????????? ???????????? ??????
     * ???????????? ???????????? ????????? ??????????????? ????????? ??????
     *
     */



    @Test
    public void ????????????_??????????????????_??????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();


        //when
        String toBePassword = "1234567890!@#!@#";
        memberService.updatePassword(PASSWORD, toBePassword, SecurityUtil.getLoginUsername());//TODO : ??????
        clear();

        //then
        Member findMember = memberRepository.findByUsername(memberSignUpDto.username()).orElseThrow(() -> new Exception());
        assertThat(findMember.matchPassword(passwordEncoder, toBePassword)).isTrue();

    }



    @Test
    public void ????????????_???????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        String updateName = "???????????????";
        memberService.update(new MemberUpdateDto(Optional.of(updateName),Optional.empty(), Optional.empty()), SecurityUtil.getLoginUsername());//TODO : ??????
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getName()).isEqualTo(updateName);
            assertThat(member.getAge()).isEqualTo(memberSignUpDto.age());
            assertThat(member.getNickName()).isEqualTo(memberSignUpDto.nickName());
        }));

    }
    @Test
    public void ????????????_???????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        String updateNickName = "???????????????";
        memberService.update(new MemberUpdateDto(Optional.empty(), Optional.of(updateNickName), Optional.empty()), SecurityUtil.getLoginUsername());//TODO : ??????
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getNickName()).isEqualTo(updateNickName);
            assertThat(member.getAge()).isEqualTo(memberSignUpDto.age());
            assertThat(member.getName()).isEqualTo(memberSignUpDto.name());
        }));

    }

    @Test
    public void ????????????_???????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        Integer updateAge = 33;
        memberService.update(new MemberUpdateDto(Optional.empty(),  Optional.empty(), Optional.of(updateAge)), SecurityUtil.getLoginUsername());//TODO : ??????
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getAge()).isEqualTo(updateAge);
            assertThat(member.getNickName()).isEqualTo(memberSignUpDto.nickName());
            assertThat(member.getName()).isEqualTo(memberSignUpDto.name());
        }));
    }


    @Test
    public void ????????????_??????????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        String updateNickName = "??????????????????";
        String updateName = "???????????????";
        memberService.update(new MemberUpdateDto(Optional.of(updateName),Optional.of(updateNickName),Optional.empty()), SecurityUtil.getLoginUsername());//TODO : ??????
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getNickName()).isEqualTo(updateNickName);
            assertThat(member.getName()).isEqualTo(updateName);

            assertThat(member.getAge()).isEqualTo(memberSignUpDto.age());
        }));

    }

    @Test
    public void ????????????_??????????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        Integer updateAge = 33;
        String updateName = "???????????????";
        memberService.update(new MemberUpdateDto(Optional.of(updateName),Optional.empty(),Optional.of(updateAge)), SecurityUtil.getLoginUsername());//TODO : ??????
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getAge()).isEqualTo(updateAge);
            assertThat(member.getName()).isEqualTo(updateName);

            assertThat(member.getNickName()).isEqualTo(memberSignUpDto.nickName());
        }));


    }
    @Test
    public void ????????????_??????????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        Integer updateAge = 33;
        String updateNickname = "???????????????";
        memberService.update(new MemberUpdateDto(Optional.empty(),Optional.of(updateNickname),Optional.of(updateAge)), SecurityUtil.getLoginUsername());//TODO : ??????
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getAge()).isEqualTo(updateAge);
            assertThat(member.getNickName()).isEqualTo(updateNickname);

            assertThat(member.getName()).isEqualTo(memberSignUpDto.name());
        }));

    }

    @Test
    public void ????????????_????????????????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        Integer updateAge = 33;
        String updateNickname = "???????????????";
        String updateName = "???????????????";
        memberService.update(new MemberUpdateDto(Optional.of(updateName),Optional.of(updateNickname),Optional.of(updateAge)), SecurityUtil.getLoginUsername());//TODO : ??????
        clear();

        //then
        memberRepository.findByUsername(memberSignUpDto.username()).ifPresent((member -> {
            assertThat(member.getAge()).isEqualTo(updateAge);
            assertThat(member.getNickName()).isEqualTo(updateNickname);
            assertThat(member.getName()).isEqualTo(updateName);
        }));
    }

    /**
     * ????????????
     * ??????????????? ??????????????? ???????????? ?????? ??????
     */

    @Test
    public void ????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        memberService.withdraw(PASSWORD, SecurityUtil.getLoginUsername());//TODO : ??????

        //then
        assertThat(assertThrows(Exception.class, ()-> memberRepository.findByUsername(memberSignUpDto.username()).orElseThrow(() -> new Exception("????????? ????????????"))).getMessage()).isEqualTo("????????? ????????????");

    }

    @Test
    public void ????????????_??????_???????????????_??????????????????() throws Exception {
        //given
        setMember();

        //when, then TODO: ??????
        assertThat(assertThrows(MemberException.class ,() -> memberService.withdraw(PASSWORD+"1", SecurityUtil.getLoginUsername())).getExceptionType()).isEqualTo(MemberExceptionType.WRONG_PASSWORD);

    }




    @Test
    public void ??????????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();
        Member member = memberRepository.findByUsername(memberSignUpDto.username()).orElseThrow(() -> new Exception());
        clear();

        //when
        MemberInfoDto info = memberService.getInfo(member.getId());

        //then
        assertThat(info.getUsername()).isEqualTo(memberSignUpDto.username());
        assertThat(info.getName()).isEqualTo(memberSignUpDto.name());
        assertThat(info.getAge()).isEqualTo(memberSignUpDto.age());
        assertThat(info.getNickName()).isEqualTo(memberSignUpDto.nickName());
    }

    @Test
    public void ???????????????() throws Exception {
        //given
        MemberSignUpDto memberSignUpDto = setMember();

        //when
        MemberInfoDto myInfo = memberService.getMyInfo();

        //then
        assertThat(myInfo.getUsername()).isEqualTo(memberSignUpDto.username());
        assertThat(myInfo.getName()).isEqualTo(memberSignUpDto.name());
        assertThat(myInfo.getAge()).isEqualTo(memberSignUpDto.age());
        assertThat(myInfo.getNickName()).isEqualTo(memberSignUpDto.nickName());

    }

}