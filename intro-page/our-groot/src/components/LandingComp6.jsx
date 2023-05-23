import { useEffect } from "react";
import styled from "styled-components";
// import { styled as muistyled } from "@mui/material/styles";
// import Button, { ButtonProps } from "@mui/material/Button";
// import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
// import KeyboardDoubleArrowDownIcon from "@mui/icons-material/KeyboardDoubleArrowDown";
// import shiba1 from "/assets/img/shiba_landing.png";
// import shiba2 from "/assets/img/shiba_landing2.png";
// import shiba3 from "/assets/img/shiba_landing3.png";
// import shiba4 from "/assets/img/shiba_profile.png";
import AOS from "aos";
import comp6_1 from "../assets/comp6_1.png";
import comp6_2 from "../assets/comp6_2.png";

const LandingComp6 = () => {
  useEffect(() => {
    AOS.init();
  });

  return (
    <Background style={{ textAlign: "left" }}>
      <TitleDescriptionWrapper data-aos="fade-up">
        <Content>
          <StyledTitle>식물 백과사전</StyledTitle>
          <ContentTitle>
            <ContentDescription>
              관심있는 식물을
              <br />
              빠르게 검색해보세요
            </ContentDescription>
          </ContentTitle>
          <ContentScript>
            <StyledDescription>
              필터링 검색 기능으로
              <br /> 원하는 조건의 식물을 찾을 수 있습니다.
              <br /> 식물 이름을 모른다면
              <br />
              이미지 검색으로 식별하여 식물을 찾아드려요.
            </StyledDescription>
          </ContentScript>
        </Content>
        <ImgWrapper>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="300"
            data-aos-duration="1500"
            src={comp6_1}
            style={{ width: "300px", top: "5vh", left: "10vw", zIndex: 2 }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="500"
            data-aos-duration="1500"
            src={comp6_2}
            style={{ width: "300px", top: "15vh", left: "28vw", zIndex: 3 }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1000"
            data-aos-duration="1500"
            // src={shiba3}
            style={{ width: "500px", top: "40vh", left: "15vw", zIndex: 4 }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1500"
            data-aos-duration="1500"
            // src={shiba4}
            style={{ width: "300px", top: "45vh", left: "30vw", zIndex: 5 }}
          ></CustomedImage>
        </ImgWrapper>
      </TitleDescriptionWrapper>
    </Background>
  );
};
export default LandingComp6;

const Background = styled.div`
  background: #f3f3f3;
  /* padding: 15vh 15vh 0px 15vh; */
  z-index: 0;
  height: 100vh;
`;

const TitleDescriptionWrapper = styled.div`
  top: 10em;
  padding-top: 10vh;
  margin-bottom: 30em;
  display: flex;
`;

const StyledTitle = styled.h2`
  font-family: "One-Mobile-POP";
  padding-top: 3em;
  padding-bottom: 20px;
  margin-top: 20px;
  color: #639a67;
  font-size: xx-large;
`;

const ContentTitle = styled.h2`
  font-family: "One-Mobile-POP";
  font-size: x-large;
`;

const ContentDescription = styled.h2``;

const StyledDescription = styled.h2`
  width: "10px";
  font-family: "ONE-Mobile-Regular";
  color: #828282;
`;

const ImgWrapper = styled.div`
  position: relative;
  width: 500px;
`;

const CustomedImage = styled.img`
  position: absolute;
  top: 15em;
  left: 15em;
  width: 500px;
`;

const Content = styled.div`
  padding-left: 13vw;
  padding-top: 5vh;
  /* margin-left: 5em; */
`;

const ContentScript = styled.div`
  /* width: 400px; */
`;

// const Bounce = styled.div(
//   tw`animate-bounce`,
//   css`
//     position: "absolute";
//     /* left: 43%; */
//     /* right: 50%; */
//     /* top: 80%; */
//     bottom: 10em;
//     /* margin-right: 50%; */
//     text-align: center;
//   `
// );
