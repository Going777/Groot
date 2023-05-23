import { useEffect } from "react";
import styled from "styled-components";
// import { styled as muistyled } from "@mui/material/styles";
// import Button, { ButtonProps } from "@mui/material/Button";
// import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore"; // import landing1 from "/assets/img/land_animation1.png";
// import landing2 from "/assets/img/land_animation2.png";
// import landing3 from "/assets/img/land_animation3.png";
// import landing4 from "/assets/img/land_animation4.png";
// import three from "../assets/cactuso_0.png";
// import four from "../assets/mole_0.png";
// import one from "../assets/rabby_0.png";
// import five from "../assets/sprout_0.png";
// import two from "../assets/tree_0.png";
import group from "../assets/Group 243.png";
import qr from "../assets/qrcode.png";
import google_play from "../assets/google_play.png";

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
        {/* <CustomedImage
          data-aos="fade-up"
          data-aos-delay="300"
          data-aos-duration="1500"
          src={logo}
          style={{
            // width: "300px",
            // height: "150px",
            top: "5vh",
            left: "35vw",
            zIndex: 2,
          }}
        ></CustomedImage> */}

        <ImgWrapper>
          <StyledDescription
            style={{
              // width: "300px",
              // height: "150px",
              top: "5vh",
              left: "0vw",
            }}
          >
            화분 속 작은 친구<br></br>
            GROOT에서 만나보세요
          </StyledDescription>
          {/* <CustomedImage
            data-aos="fade-up"
            data-aos-delay="300"
            data-aos-duration="1500"
            src={one}
            style={{
              width: "150px",
              height: "100px",
              top: "10vh",
              left: "23vw",
              zIndex: 2,
            }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="500"
            data-aos-duration="1500"
            src={two}
            style={{
              width: "150px",
              height: "100px",
              top: "10vh",
              left: "34vw",
              zIndex: 3,
            }}
          ></CustomedImage> */}
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1000"
            data-aos-duration="1500"
            src={group}
            style={{
              width: "900px",
              height: "500px",
              top: "35vh",
              left: "23vw",
              zIndex: 4,
            }}
          ></CustomedImage>
          {/* <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1500"
            data-aos-duration="1500"
            src={four}
            style={{
              width: "150px",
              height: "100px",
              top: "10vh",
              left: "54vw",
              zIndex: 5,
            }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1500"
            data-aos-duration="1500"
            src={five}
            style={{
              width: "150px",
              height: "100px",
              top: "10vh",
              left: "64vw",
              zIndex: 5,
            }}
          ></CustomedImage> */}
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1500"
            data-aos-duration="1500"
            src={qr}
            style={{
              width: "100px",
              height: "100px",
              top: "80vh",
              left: "90vw",
              zIndex: 6,
            }}
          ></CustomedImage>
          <CustomedImage
            data-aos="fade-up"
            data-aos-delay="1500"
            data-aos-duration="1500"
            src={google_play}
            style={{
              width: "300px",
              height: "100px",
              top: "80vh",
              left: "70vw",
              zIndex: 5,
            }}
          ></CustomedImage>
        </ImgWrapper>
      </TitleDescriptionWrapper>
      <Bounce>
        <ExpandMoreIcon sx={IconStyle} />
      </Bounce>
    </Background>
  );
};
export default LandingComp1;

const Background = styled.div`
  background: #fbfbfb;
  /* padding: 15vh 15vh 0px 15vh; */
  /* z-index: 0; */
  height: 100vh;
`;

const TitleDescriptionWrapper = styled.div`
  top: 10vh;
  padding-top: 10vh;
  margin-bottom: 30em;
  display: flex;
  justify-content: center;
`;

const StyledTitle = styled.h2`
  font-family: "One-Mobile-POP";
  padding-top: 3em;
  padding-bottom: 20px;
  margin-top: 20px;
`;

const StyledDescription = styled.h1`
  width: "100vw";
  font-family: "ONE-Mobile-Regular";
  /* top: "5vh";
  left: "35vw"; */
`;

const ImgWrapper = styled.div`
  /* position: relative; */
  width: 100vw;
  text-align: center;
`;

const CustomedImage = styled.img`
  position: absolute;
  top: 15em;
  left: 15em;
  width: 100vw;
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
  top: "95vh",
  fontSize: "70px",
  color: "#C4C4C4",
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
