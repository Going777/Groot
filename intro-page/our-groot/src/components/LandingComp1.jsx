import { useEffect } from "react";
import styled from "styled-components";
import AOS from "aos";



const LandingComp1 = () => {
  useEffect(() => {
    AOS.init();
  });

  return (
    <Background style={{ textAlign: "center" }}>
      <TitleDescriptionWrapper data-aos="fade-up">
        <Content>
          <StyledTitle>
            사진을 찍어 식물을 식별하고, <br></br> 화분을 등록해보세요.
            <br></br> 총 <Highlight>36종</Highlight>의 캐릭터가 여러분을
            기다리고 있습니다.
          </StyledTitle>
        </Content>
      </TitleDescriptionWrapper>
    </Background>
  );
};
export default LandingComp1;



const Background = styled.div`
  height: 70vh;
  background: #f3f3f3;
`;

const TitleDescriptionWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%; 
`;

const Content = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%; 
`;

const StyledTitle = styled.h2`
  font-family: 'Jeju Gothic', sans-serif;
  width: 100%;
  text-align: center;
  margin: auto auto;
  color: #000000;
  font-size: 30px;
`;

const Highlight = styled.span`
  font-family: "JejuGothic";
  color: #639a67;
`;
