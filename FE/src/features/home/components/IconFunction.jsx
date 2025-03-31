import Netflix from "../assets/Netflix.png"
import Disney from "../assets/Disney.png"
import Wave from "../assets/Wave.png"
import Tving from "../assets/Tving.png"
import Coupang from "../assets/Coupang.png"
import Watcha from "../assets/Watcha.png"
import Melon from "../assets/Melon.png"
import Jenie from "../assets/Jenie.png"
import Youtube from "../assets/Youtube.png"
import spotify from '../assets/spotify.png'
import Millie from "../assets/Millie.png"
import GPT from "../assets/GPT.png"
import Claude from "../assets/Claude.png"
import Perplexity from "../assets/Perplexity.png"


export default function IconFunction(name) {
    try {
        switch (name) {
            case "넷플릭스":
                return Netflix;
            case "디즈니+":
                return Disney;
            case "웨이브":
                return Wave;
            case "티빙":
                return Tving;
            case "쿠팡플레이":
                return Coupang;
            case "왓챠":
                return Watcha;
            case "멜론":
                return Melon;
            case "지니뮤직":
                return Jenie;
            case "유투브뮤직":
                return Youtube;
            case "스포티파이":
                return spotify;
            case "밀리의서재":
                return Millie;
            case "GPT":
                return GPT;
            case "Claude":
                return Claude;
            case "Perplexity":
                return Perplexity;
            default:
                return null;
        }
    } catch (error) {
        console.error("IconFunction 오류:", error);
        return null;
    }
}