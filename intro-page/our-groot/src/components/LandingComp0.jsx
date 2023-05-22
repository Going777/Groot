import { useEffect } from "react";
import styled from "styled-components";
// import { styled as muistyled } from "@mui/material/styles";
// import Button, { ButtonProps } from "@mui/material/Button";
// import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import KeyboardDoubleArrowDownIcon from "@mui/icons-material/KeyboardDoubleArrowDown";
// import landing1 from "/assets/img/land_animation1.png";
// import landing2 from "/assets/img/land_animation2.png";
// import landing3 from "/assets/img/land_animation3.png";
// import landing4 from "/assets/img/land_animation4.png";
import three from "../assets/cactuso_0.png";
import four from "../assets/mole_0.png";
import one from "../assets/rabby_0.png";
import five from "../assets/sprout_0.png";
import two from "../assets/tree_0.png";
import AOS from "aos";

const LandingComp1 = () => {
  useEffect(() => {
    AOS.init();
  });

  return (
    <Background style={{ textAlign: "center" }}>
      <TitleDescriptionWrapper data-aos="fade-up">
        {/* <Content>
          <StyledTitle>
            애니메이션 속 주인공이 되어<br></br> 캐릭터들과 영어로 대화해보세요
          </StyledTitle>
          <ContentScript>
            <StyledDescription>스크립트를 보고 따라 읽으면서</StyledDescription>
            <StyledDescription>영어 발음을 평가하고</StyledDescription>
            <StyledDescription>보상도 얻을 수 있습니다.</StyledDescription>
          </ContentScript>
        </Content> */}
        <ImgWrapper>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="300"
            data-aos-duration="1500"
            src={one}
            style={{
              width: "200px",
              height: "150px",
              top: "10vh",
              left: "10vw",
              zIndex: 2,
            }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="500"
            data-aos-duration="1500"
            src={two}
            style={{
              width: "200px",
              height: "150px",
              top: "10vh",
              left: "20vw",
              zIndex: 3,
            }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1000"
            data-aos-duration="1500"
            src={three}
            style={{
              width: "150px",
              height: "150px",
              top: "10vh",
              left: "32vw",
              zIndex: 4,
            }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1500"
            data-aos-duration="1500"
            src={four}
            style={{
              width: "200px",
              height: "150px",
              top: "10vh",
              left: "40vw",
              zIndex: 5,
            }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1500"
            data-aos-duration="1500"
            src={five}
            style={{
              width: "200px",
              height: "150px",
              top: "10vh",
              left: "50vw",
              zIndex: 5,
            }}
          ></CustomedImage>
        </ImgWrapper>
      </TitleDescriptionWrapper>
      <Bounce>
        <KeyboardDoubleArrowDownIcon sx={IconStyle} />
      </Bounce>
    </Background>
  );
};
export default LandingComp1;

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
  text-align: center;
`;

const CustomedImage = styled.img`
  position: absolute;
  top: 15em;
  left: 15em;
  width: 500px;
`;

const Content = styled.div`
  padding-left: 10vw;
  padding-top: 20vh;
  /* margin-left: 5em; */
`;

const IconStyle = {
  // marginTop: "23vh",
  // marginLeft: "0.7em",
  // right: "50px",
  // textAlign: "center",
  fontSize: "70px",
  color: "#005112",
};

const ContentScript = styled.div`
  /* width: 400px; */
`;

const Bounce = styled.div`
  position: "absolute";
  /* left: 43%; */
  /* right: 50%; */
  /* top: 80%; */
  bottom: 10em;
  /* margin-right: 50%; */
  text-align: center;
`;
