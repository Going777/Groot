import { useEffect } from "react";
import styled from "styled-components";
// import { styled as muistyled } from "@mui/material/styles";
// import Button, { ButtonProps } from "@mui/material/Button";
// import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
// import KeyboardDoubleArrowDownIcon from "@mui/icons-material/KeyboardDoubleArrowDown";
// import drawing1 from "/assets/img/drawing1.png";
// import drawing2 from "/assets/img/drawing2.png";
// import drawing3 from "/assets/img/drawing3.png";
// import drawing4 from "/assets/img/drawing4.png";
import AOS from "aos";

const LandingComp3 = () => {
  useEffect(() => {
    AOS.init();
  });

  return (
    <Background style={{ textAlign: "center" }}>
      <TitleDescriptionWrapper data-aos="fade-up">
        <ImgWrapper>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="300"
            data-aos-duration="1500"
            // src={drawing4}
            style={{ width: "500px", top: "10vh", left: "10vw", zIndex: 2 }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="500"
            data-aos-duration="1500"
            // src={drawing2}
            style={{ width: "500px", top: "20vh", left: "25vw", zIndex: 3 }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1000"
            data-aos-duration="1500"
            // src={drawing3}
            style={{ width: "500px", top: "30vh", left: "15vw", zIndex: 4 }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1500"
            data-aos-duration="1500"
            // src={drawing1}
            style={{ width: "500px", top: "45vh", left: "20vw", zIndex: 5 }}
          ></CustomedImage>
        </ImgWrapper>
        <Content>
          <StyledTitle>
            주어진 영어단어를 보고
            <br /> 그림을 그려보세요
          </StyledTitle>
          <ContentScript>
            <StyledDescription>영어 단어를 해석하고</StyledDescription>
            <StyledDescription>제한시간 안에 그림을 그리며</StyledDescription>
            <StyledDescription>단어공부를 할 수 있습니다.</StyledDescription>
          </ContentScript>
        </Content>
      </TitleDescriptionWrapper>
    </Background>
  );
};
export default LandingComp3;

const Background = styled.div`
  background: #fbfbfb;
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
`;
const StyledDescription = styled.h2`
  width: "10px";
  font-family: "ONE-Mobile-Regular";
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
  padding-left: 20vw;
  padding-top: 15vh;
  margin-left: 25em;
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
