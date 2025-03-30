import { useEffect, useState } from "react"
import CustomHeader from "../../../components/CustomHeader"
import Api from "../../../services/Api"
import CustomModal from "../../../components/CustomModal"

export default function SubScribe() {
    const [buttonChange, setButtonChange] = useState(false)
    const [subScribeCost, setSubScribeCost] = useState(null)
    const [subScribeList, setSubScribeList] = useState([])
    const [isModal, setIsModal ] =useState(false)

    //모달 열기
    function onOpenModal() {
        setIsModal(true)
    }

    //모달 닫기
    function onCloseModal() {
        setIsModal(false)
    }

    function spend() {
        const fetchData = async () => {
            try {
                const response = await Api.post("api/subscribe",{
                    "subscribeName": "Disney Plus",
                    "subscribePrice": 11800,
                    "paymentDate": "2023.03.30"
                })
                console.log(response)
            } catch (error) {
                console.log(error)
            }
        }
        fetchData()
    }

    //결제일 순으로 우선 받음
    useEffect(()=> {
        const fetchData = async () => {
            try {
                const response = await Api.get("api/subscribe/day")
                console.log(response)
                if (response.data.status === "success") {
                    setSubScribeList(response.data.data.subscriptions)
                    const totallCost = response.data.data.subscriptions.reduce((sum,price)=> {
                        return sum+ price.subscribePrice
                    },0)
                    setSubScribeCost(totallCost)
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData()
    },[])

    // 결제일순 
    function payButtonChangeHandler() {
        setButtonChange(false)
        const fetchData = async () => {
            try {
                const response = await Api.get("api/subscribe/day")
                if (response.data.status === "success") {
                    setSubScribeList(response.data.data.subscriptions)
                    const totallCost = response.data.data.subscriptions.reduce((sum,price)=> {
                        return sum+ price.subscribePrice
                    },0)
                    setSubScribeCost(totallCost)
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData()
    }

    // 금액순
    function costButtonChangeHandler() {
        setButtonChange(true)
        const fetchData = async () => {
            try {
                const response = await Api.get("api/subscribe/expensive")
                if (response.data.status === "success") {
                    setSubScribeList(response.data.data.subscriptions)
                    const totallCost = response.data.data.subscriptions.reduce((sum,price)=> {
                        return sum+ price.subscribePrice
                    },0)
                    setSubScribeCost(totallCost)
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData()
    }

    return(
        <div className="w-full flex flex-col gap-4 relative">
            <CustomHeader title="티끌냥"/>
            
            <div className="absolute left-0 right-0 px-5 top-16 flex flex-col gap-4">
                <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 mx-auto">
                    <div>
                        <p>유저님의 구독정보를 알려드릴께요</p>
                        <p>잘 사용하지 않는 서비스는 혜지해보세요</p>
                    </div>
                    <div>
                        <p>총 금액 {subScribeCost}</p>
                    </div>
                </div>

                <div className="flex flex-row justify-between">
                    <div className="flex gap-2">
                        <button className={(!buttonChange) ? "blackButton active:bg-gray-900" : "whiteButton active:bg-white"} onClick={payButtonChangeHandler}>결제일순</button>
                        <button className={(buttonChange)?"blackButton active:bg-[#242424]" : "whiteButton active:bg-white"} onClick={costButtonChangeHandler}>금액순</button>
                    </div>
                    <button className="h-[25px] px-[15px] py-[2px] text-[15px] font-medium text-white flex justify-center items-center rounded-[100px] bg-[#ff957a] font-medium font-['Pretendard_Thin']" onClick={onOpenModal}>등록하기</button>
                </div>
                <CustomModal isOpen={isModal} onClose={onCloseModal} title="구독 등록하기">
                    <div className="w-full flex flex-col gap-4">
                        <div className="w-full flex items-center justify-center">
                            <p>항목 : </p>
                            <input type="text" placeholder="xxx" />
                        </div>
                        <div className="w-full flex items-center justify-center">
                            <p>금액 : </p>
                            <input type="text" placeholder="1000" />
                        </div>
                        <div>
                            <input type="date" />
                        </div>
                        <div className="flex justify-center">
                            <button className="customButton" onClick={spend}>등록하기</button>
                        </div>
                    </div>
                </CustomModal>

            </div>


            <div className="w-full flex flex-col gap-4 mt-60">
                {(subScribeList.length === 0)?
                <div>
                    <p>기록이 존재하지 않습니다</p>
                    <p>구독 내역을 등록해 주세요</p>
                </div>
                :(!!buttonChange) ? Array.isArray(subScribeList) && subScribeList.map((item)=> (
                    <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4">
                        {item.paymentDate} {item.subscribeName} {item.subscribePrice}
                    </div>
                ))
                : Array.isArray(subScribeList) && subScribeList.map((item)=> (
                    <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4">
                        {item.paymentDate} {item.subscribeName} {item.subscribePrice} {item.daysRemaining}
                    </div>
                ))
            }
            </div>

        </div>
    )
}