import CryingCat from "../assets/shared/crying_cat.png"
import GriningCat from "../assets/shared/grinning_cat.png"
import KissingCat from "../assets/shared/kissing_cat.png"
import PoutingCat from "../assets/shared/pouting_cat.png"
export default function getEmoji(name) {
    switch (name) {
        case 0:
            return KissingCat
        case 1:
            return GriningCat
        case 2:
            return CryingCat
        case 3:
            return PoutingCat
        
    }
}