import { useEffect, useState } from "react"
import CustomHeader from "../../../components/CustomHeader"
import Api from "../../../services/Api"
import CustomModal from "../../../components/CustomModal"
import { useSelector } from "react-redux"
import IconFunction from "./IconFunction"
import {motion} from "framer-motion"

export default function SubScribe() {
    const {nickName} = useSelector(state=> state.user)
    const [buttonChange, setButtonChange] = useState(false)
    const [subScribeCost, setSubScribeCost] = useState(null)
    const [subScribeList, setSubScribeList] = useState([])
    const [isModal, setIsModal ] =useState(false)
    const [swipedItems, setSwipedItems] = useState({});
    const [isSelectOption, setIsSelectOption] = useState({
        "subscribeName":"",
        "subscribePrice":"",
        "paymentDate":""
    })

    // 드래그 끝까지 갔을때 관리
    const handleDragEnd = (id, info) => {
        // 스와이프가 왼쪽으로 충분히 이동했을 때만 열린 상태로 설정
        if (info.offset.x < -30) {
          setSwipedItems(prev => ({ ...prev, [id]: true }));
        } else {
          setSwipedItems(prev => ({ ...prev, [id]: false }));
        }
      };

    // 입력값 변경 
    function onChangeHandler(e) {
        const {name,value} = e.target
        console.log(name,value)
        setIsSelectOption({
            ...isSelectOption,
            [name]:value
        })
    }

    // 삭제 구현
    function deleteHandler(id) {
        const fetchData = async () => {
            try {
                const response = await Api.delete(`api/subscribe/${id}`)
                if (response.data.status === "success") {
                    window.alert("삭제에 성공하셨습니다")
                    const updateList = subScribeList.filter(item => item.subscribeId !== id)
                    setSubScribeList(updateList)
                    const newTotalCost = updateList.reduce((sum,item)=> {
                        return sum + item.subscribePrice
                    },0)
                    setSubScribeCost(newTotalCost)
                    const newSwipedItems = { ...swipedItems };
                    delete newSwipedItems[id];
                    setSwipedItems(newSwipedItems);
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData()
    }

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
                    "subscribeName": isSelectOption.subscribeName,
                    "subscribePrice": isSelectOption.subscribePrice,
                    "paymentDate": isSelectOption.paymentDate
                })
                if (response.data.status === "success"){
                    window.alert("구독 등록에 성공하셨습니다") 
                    setSubScribeList([
                        ...subScribeList,
                        {
                            "subscribeName": isSelectOption.subscribeName,
                            "subscribePrice": isSelectOption.subscribePrice,
                            "paymentDate": isSelectOption.paymentDate
                        }
                    ])
                    const currentCost = parseInt(subScribeCost,10) + parseInt(isSelectOption.subscribePrice,10)
                    console.log(currentCost)
                    setSubScribeCost(currentCost)
                }
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
            
            <div className="absolute left-0 right-0 top-16 flex flex-col gap-4">
                <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 mx-auto flex flex-col gap-2">
                    <div>
                        <div className="text-left">
                            <span className="text-[#ff957a] text-ml">{nickName}</span>
                            <span >의</span>
                        </div>
                        <p className="text-left"> 구독정보를 알려드릴께요</p>
                        <p className="text-left">잘 사용하지 않는 서비스는 혜지해보세요</p>
                    </div>
                    <div>
                        <span className="text-xl">총 금액 </span>
                        <span className="text-xl text-[#ff957a]">{subScribeCost}</span>
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
                        <select name="subscribeName" onChange={onChangeHandler} >
                            <option value="" disabled selected>구독을 선택해주세요</option>
                            <option value="넷플릭스">넷플릭스</option>
                            <option value="디즈니+">디즈니+</option>
                            <option value="웨이브">웨이브</option>
                            <option value="티빙">티빙</option>
                            <option value="쿠팡플레이">쿠팡플레이</option>
                            <option value="왓챠">왓챠</option>
                            <option value="멜론">멜론</option>
                            <option value="지니뮤직">지니뮤직</option>
                            <option value="유투브뮤직">유투브뮤직</option>
                            <option value="스포티파이">스포티파이</option>
                            <option value="밀리의서재">밀리의서재</option>
                            <option value="GPT">GPT</option>
                            <option value="Claude">Claude</option>
                            <option value="Perplexity">Perplexity</option>
                        </select>
                        {(!isSelectOption)?"":
                        <div className="w-full flex flex-col gap-4">
                            <div>
                                {isSelectOption.subscribeName && (
                                    <div className="flex justify-center">
                                        <img 
                                            src={IconFunction(isSelectOption.subscribeName)} 
                                            alt={`${isSelectOption.subscribeName} 구독 이미지`} 
                                            style={{ maxWidth: '100px', maxHeight: '100px' }} 
                                        />
                                    </div>
                                )}
                            </div>
                            <div className="w-full flex items-center justify-center justify-between">
                                <p className="w-3/10 text-xl">항목 : </p>
                                <p className="border p-1 w-7/10">{isSelectOption.subscribeName}</p>
                            </div>
                            <div className="w-full flex items-center justify-center justify-between">
                                <p className="text-xl w-3/10">금액 : </p>
                                <input className=" border p-1 w-7/10" type="text" placeholder="1000" name="subscribePrice" onChange={onChangeHandler} value={isSelectOption.subscribePrice}/>
                            </div>
                            <div className="w-full">
                                <input type="date" name="paymentDate" onChange={onChangeHandler} value={isSelectOption.paymentDate}/>
                            </div>
                            <div className="flex justify-center">
                                <button className="customButton" onClick={spend}>등록하기</button>
                            </div>
                        </div>
                        }
                    </div>
                </CustomModal>

            </div>


            <div className="w-full flex flex-col gap-4 mt-65">
                {(subScribeList.length === 0)?
                <div>
                    <p>기록이 존재하지 않습니다</p>
                    <p>구독 내역을 등록해 주세요</p>
                </div>
                :(!!buttonChange) ? Array.isArray(subScribeList) && subScribeList.map((item, index)=> (
                    <div className="relative overflow-hidden" key={index}>
                    {/* 뒤에 위치할 삭제 아이콘 영역 */}
                    <div className="absolute right-0 top-0 bottom-0 flex items-center justify-center w-16 z-0 rounded-[6px] bg-white" onClick={()=> deleteHandler(item.subscribeId)}>
                    <svg 
                        xmlns="http://www.w3.org/2000/svg" 
                        width="24" 
                        height="24" 
                        viewBox="0 0 24 24" 
                        fill="none" 
                        stroke="red" 
                        strokeWidth="2" 
                        strokeLinecap="round" 
                        strokeLinejoin="round"
                        >
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        <line x1="10" y1="11" x2="10" y2="17"></line>
                        <line x1="14" y1="11" x2="14" y2="17"></line>
                        </svg>
                    </div>
                    
                    {/* 스와이프 가능한 콘텐츠 - z-index와 배경색 추가 */}
                    <motion.div 
                        className="w-full flex bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 justify-between items-center relative" 
                        style={{ zIndex: 2 }}
                        drag="x" 
                        dragConstraints={{ left: -64, right: 0 }} 
                        animate={{ x: swipedItems[item.subscribeId] ? -64 : 0 }}
                        onDragEnd={(_, info) => handleDragEnd(item.subscribeId, info)}
                        dragElastic={0.1}
                    >
                      {/* 왼쪽 구독 정보 영역 */}
                      <div className="w-3/10">
                            <p>{item.subscribeName}</p>
                            </div>
                            
                            {/* 오른쪽 가격 및 이미지 영역 */}
                        <div className="w-7/10 flex justify-end items-center gap-4">
                            {/* 가격 표시 */}
                            <div className="flex justify-center items-center">
                                <p className="text-xl">{item.subscribePrice} 원</p>
                            </div>
                      <div className="flex justify-center">
                        <img 
                          src={IconFunction(item.subscribeName)} 
                          alt={`${item.subscribeName} 구독 이미지`} 
                          style={{ maxWidth: '50px', maxHeight: '50px' }} 
                          />
                      </div>
                    </div>
                    </motion.div>
                  </div>
                ))
                : Array.isArray(subScribeList) && subScribeList.map((item, index)=> (
                    <div className="relative overflow-hidden" key={index}>
                        {/* 뒤에 위치할 삭제 아이콘 영역 - z-index 낮게 설정 */}
                        <div className="absolute right-0 top-0 bottom-0 flex items-center justify-center w-16 rounded-[6px] bg-white" style={{ zIndex: 1 }} onClick={()=> deleteHandler(item.subscribeId)}>
                            <svg 
                            xmlns="http://www.w3.org/2000/svg" 
                            width="24" 
                            height="24" 
                            viewBox="0 0 24 24" 
                            fill="none" 
                            stroke="red" 
                            strokeWidth="2" 
                            strokeLinecap="round" 
                            strokeLinejoin="round"
                            >
                            <polyline points="3 6 5 6 21 6"></polyline>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                            <line x1="10" y1="11" x2="10" y2="17"></line>
                            <line x1="14" y1="11" x2="14" y2="17"></line>
                            </svg>
                        </div>
                        
                        {/* 스와이프 가능한 콘텐츠 - z-index 높게 설정 */}
                        <motion.div 
                            className="w-full flex bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 justify-between items-center relative" 
                            style={{ zIndex: 2 }}
                            drag="x" 
                            dragConstraints={{ left: -64, right: 0 }} 
                            animate={{ x: swipedItems[item.subscribeId] ? -64 : 0 }}
                            onDragEnd={(_, info) => handleDragEnd(item.subscribeId, info)}
                            dragElastic={0.1}
                        >
                            {/* 왼쪽 구독 정보 영역 */}
                      <div className="w-3/10">
                            <p>D-{item.daysRemaining}</p>
                            <p>{item.subscribeName}</p>
                            </div>
                            
                            {/* 오른쪽 가격 및 이미지 영역 */}
                            <div className="w-7/10 flex justify-end items-center gap-4">
                                {/* 가격 표시 */}
                                <div className="flex justify-center items-center">
                                    <p className="text-xl">{item.subscribePrice} 원</p>
                                </div>
                      <div className="flex justify-center">
                        <img 
                          src={IconFunction(item.subscribeName)} 
                          alt={`${item.subscribeName} 구독 이미지`} 
                          style={{ maxWidth: '50px', maxHeight: '50px' }} 
                          />
                      </div>
                    </div>
                    </motion.div>
                  </div>
                ))
            }
            </div>

        </div>
    )
}