import { useState } from "react"
import CustomHeader from "../../../components/CustomHeader"

export default function SubScribe() {
    const [subScribeCost, setSubScribeCost] = useState(11000)
    return(
        <div>
            <CustomHeader title="티끌냥"/>

            <div>
                <p>유저님의 구독정보를 알려드릴께요</p>
                <p>잘 사용하지 않는 서비스는 혜지해보세요</p>
            </div>

            <div>
                <p>초 금액 {}</p>
            </div>
        </div>
    )
}