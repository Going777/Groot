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
import comp5_1 from "../assets/comp5_1.png";
import comp5_2 from "../assets/comp5_2.png";
import { color } from "@mui/system";

const LandingComp5 = () => {
  useEffect(() => {
    AOS.init();
  });

  return (
    <Background style={{ textAlign: "right" }}>
      <TitleDescriptionWrapper>
        <ImgWrapper>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="500"
            data-aos-duration="2500"
            src={comp5_1}
            style={{ width: "500px", top: "20vh", left: "20vw", zIndex: 2 }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="500"
            data-aos-duration="2500"
            src={comp5_2}
            style={{ width: "500px", top: "35vh", left: "20vw", zIndex: 3 }}
          ></CustomedImage>
        </ImgWrapper>
        <Content data-aos="fade-left" data-aos-duration="2500">
          <StyledTitle>일정 관리</StyledTitle>
          <ContentTitle>
            <ContentDescription>
              잊기 쉬운 물 주기를
              <br />
              알림으로 받아보세요
            </ContentDescription>
          </ContentTitle>
          <ContentScript>
            <StyledDescription>
              식물 별 가드닝 활동을
              <br />
              캘린더로 미리 확인하고,
              <br />
              알림으로 한 번 더 체크할 수 있습니다.
            </StyledDescription>
          </ContentScript>
        </Content>
      </TitleDescriptionWrapper>
    </Background>
  );
};
export default LandingComp5;

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
  margin-right: 20vw;
  padding-left: 20vw;
  padding-top: 15vh;
  margin-left: 25em;
`;

const ContentScript = styled.div`
  width: 600px;
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
