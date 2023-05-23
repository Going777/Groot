import { useEffect } from "react";
import styled from "styled-components";
// import { styled as muistyled } from "@mui/material/styles";
// import Button, { ButtonProps } from "@mui/material/Button";
// import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
// import KeyboardDoubleArrowDownIcon from "@mui/icons-material/KeyboardDoubleArrowDown";
import comp7 from "../assets/comp7.png";

import AOS from "aos";
import "aos/dist/aos.css";
import { color } from "@mui/system";

const LandingComp7 = () => {
  useEffect(() => {
    AOS.init();
  });

  return (
    <Background style={{ textAlign: "right" }}>
      <TitleDescriptionWrapper data-aos="fade-up">
        <ImgWrapper>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="500"
            data-aos-duration="2500"
            src={comp7}
            style={{ width: "300px", top: "5vh", left: "25vw", zIndex: 2 }}
          ></CustomedImage>
        </ImgWrapper>
        <Content
          data-aos="fade-left"
          data-aos-delay="500"
          data-aos-duration="2500"
        >
          <StyledTitle>커뮤니티</StyledTitle>
          <ContentTitle>
            <ContentDescription>
              정보를 공유하고
              <br />
              화분을 나눔해보세요
            </ContentDescription>
          </ContentTitle>
          <ContentScript>
            <StyledDescription>
              식물을 키우다 보면 생기는 궁금증,
              <br />
              이제 혼자 고민하지 말고 같이 공유해요.
              <br />
              주변 이웃들에게 화분을 나눔할 수도 있습니다.
            </StyledDescription>
          </ContentScript>
        </Content>
      </TitleDescriptionWrapper>
    </Background>
  );
};
export default LandingComp7;

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
