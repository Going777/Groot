// import React from "react";
import styled from "styled-components";
// import { styled as muistyled } from "@mui/material/styles";
// import Button, { ButtonProps } from "@mui/material/Button";
// import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
// import KeyboardDoubleArrowDownIcon from "@mui/icons-material/KeyboardDoubleArrowDown";
// import abc_img from "/assets/img/LOGO.png";
// import { Height } from "@mui/icons-material";

const LandingComp5 = () => {
  return (
    <Background style={{ textAlign: "center" }}>
      <TitleDescriptionWrapper>
        {/* <ImgWrapper> */}
        <CustomedImage style={{ width: "80vh" }}></CustomedImage>
        {/* </ImgWrapper> */}
      </TitleDescriptionWrapper>
      <Bounce>{/* <KeyboardDoubleArrowDownIcon sx={IconStyle} /> */}</Bounce>
    </Background>
  );
};
export default LandingComp5;

const Background = styled.div`
  background: #fbfbfb;
  padding: 15vh 15vh 0px 15vh;
  height: 100vh;
`;

const TitleDescriptionWrapper = styled.div`
  margin-top: 15vh;
  /* margin-bottom: 30em; */
  display: flex;
`;

const CustomedImage = styled.img`
  width: 500px;
`;

// const IconStyle = {
//   fontSize: "70px",
//   color: "#005112",
// };

const Bounce = styled.div`
  position: "absolute";
  /* position: fixed; */
  left: 43%;
  right: 50%;
  padding-top: 25vh;
  /* margin-left: 50vw;
  margin-right: 50vw; */
  text-align: center;
`;
