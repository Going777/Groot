import { useEffect } from "react";
import "./App.css";
// import { useNavigate } from "react-router-dom";
import AOS from "aos";
import styled from "styled-components";
import LandingComp0 from "./components/LandingComp0";
import LandingComp1 from "./components/LandingComp1";
import LandingComp2 from "./components/LandingComp2";
import LandingComp3 from "./components/LandingComp3";
import LandingComp4 from "./components/LandingComp4";
import LandingComp5 from "./components/LandingComp5";
import LandingComp6 from "./components/LandingComp6";
import LandingComp7 from "./components/LandingComp7";
import logo from "./assets/new_logo.png";
import qr from "./assets/qrcode.png";
import google_play from "./assets/google_play.png";

function App() {
  // const navigate = useNavigate();

  useEffect(() => {
    AOS.init();
  });

  return (
    <>
      <div>
        {/* <ExtraBox style={{ textAlign: "center" }}> */}
        <FloatLogo src={logo} style={{ width: "200px" }} />
        <LandingComp0 />
        <LandingComp1 />
        <LandingComp2 />
        <LandingComp3 />
        <LandingComp4 />
        <LandingComp5 />
        <LandingComp6 />
        <LandingComp7 />
        <Float>
        <a href="https://play.google.com/store/apps/details?id=com.chocobi.groot">
        <CustomedImage
            src={google_play}
            style={{ width: "105px", zIndex: 2 }}
          ></CustomedImage>
          </a>
        <CustomedImage
            src={qr}
            style={{ width: "100px",zIndex: 2 }}
          ></CustomedImage>
        </Float>
        {/* </ExtraBox> */}
      </div>
      {/* <div>
        <a href="https://vitejs.dev" target="_blank">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>OURGROOT</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>count is {count}</button>
        <p>
          Edit <code>src/App.jsx</code> and save to test HMR
        </p>
      </div>
      <p className="read-the-docs">Click on the Vite and React logos to learn more</p> */}
    </>
  );
}

export default App;

const ExtraBox = styled.div`
  height: 500vh;
`;

const Float = styled.div`
  position: fixed;
  left: 90vw;
  right: 50%;
  top: 80%;
  /* margin-left: 50vw;
  margin-right: 50vw; */
  text-align: center;
  z-index: 999;
  /* width: 120px; */
`;

const FloatLogo = styled.img`
  position: fixed;
  width: "20px";
  height: "3rem";
  left: 1em;
  top: 1em;
  z-index: 999;
  /* width: 120px; */
`;

const CustomedImage = styled.img`
  width: 500px;
`;