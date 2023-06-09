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
import comp3 from "../assets/comp3.png";

const LandingComp3 = () => {
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
            src={comp3}
            style={{ width: "300px", top: "10vh", left: "25vw", zIndex: 2 }}
          ></CustomedImage>
        </ImgWrapper>
        <Content
          data-aos="fade-left"
          data-aos-delay="500"
          data-aos-duration="2500"
        >
          <StyledTitle>AR</StyledTitle>
          <ContentTitle>
            <ContentDescription>
              화분에서 만나는
              <br />
              나만의 작은 친구
            </ContentDescription>
          </ContentTitle>
          <ContentScript>
            <StyledDescription>
              AR 기술을 활용하여 생동감있는 캐릭터를
              <br />
              식물과 함께 만날 수 있습니다.
            </StyledDescription>
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
  color: #639a67;
  font-size: xx-large;
`;

const ContentTitle = styled.h2`
  font-family: "One-Mobile-POP";
  font-size: x-large;
`;

const ContentDescription = styled.h2``;

const StyledDescription = styled.h2`
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
  margin-right: 22vw;
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
