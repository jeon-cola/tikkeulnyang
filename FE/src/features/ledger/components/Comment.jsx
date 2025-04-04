import CustomModal from "../../../components/CustomModal"
import CommentIcon from "../assets/CommentIcon.png"
import Api from "../../../services/Api"
import { useEffect, useRef, useState } from "react"
import { useSelector } from "react-redux"
import getEmoji from "../utils/getEmoji"
import Sticker from "../assets/Sticker.png"
import KissingCat from "../assets/shared/kissing_cat.png"
import GrinningCat from "../assets/shared/grinning_cat.png"
import PoutingCat from "../assets/shared/pouting_cat.png"
import CryingCat from "../assets/shared/crying_cat.png"

export default function Comment({isOpen, onClose, title, date, userId}) {
    const [list, setList] = useState([])
    const inputValue= useRef("")
    const {profileImg} = useSelector((state)=> state.user)
    const [isEmoji, setIsEmoji] = useState(false)
    const [emoji, setEmoji] = useState(9)

    // 이모지 창
    function isOpenEmoji() {
        setIsEmoji(!isEmoji)
    }

    // 댓글 달기
    function writeCommentHandler() {
        const fetchData = async () => {
            try {
                const response = await Api.post(`api/share/comments/${userId}/${date}`,{
                    "comment":inputValue.current.value,
                    "emoji":emoji
                })
                if (response.data.status === "success") {
                    setEmoji(9)
                    commentHandler()
                    inputValue.current.value=""
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData()
    }

    // 댓글 조회
    function commentHandler() {
        const fetchData = async () => {
            try {
                console.log(date)
                const response = await Api.get(`api/share/comments/${date}`)
                if (response.data.status === "success") {
                    const data = response.data.data.comments
                    console.log(data)
                    setList(data)
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData()
    }

    useEffect(()=> {
        commentHandler()
    },[])

    return(
        <div>
            <div>
                <img src={CommentIcon} alt="댓글 아이콘" onClick={commentHandler} />
            </div>
            <CustomModal isOpen={isOpen} onClose={()=>{setEmoji(9),onClose()}} title={title}>
                <div className="relative w-full gap-7 p-3">
                    {list && list.length>0 ? list.map((comment,index)=> (
                        <div key={index} className="flex justify-between ">
                            <div className="flex gap-3 ">
                                <img src={comment.profileImageUrl} alt={`${comment.userNickname}님의 이미지`} className="w-10 h-10 rounded-full border-2 border-gray-300 cursor-pointer hover:border-blue-500" />
                                <div className="flex flex-col">
                                    <p className="text-left font-semibold">{comment.userNickname}</p>
                                    <p className="text-left">{comment.comment}</p>
                                </div>
                            </div>
                            <img src={getEmoji(comment.emoji)} alt="이모지 아이콘" className="w-10 h-10"/>
                        </div>
                    )) : <div>
                            <p className="text-xl">아직 댓글이 없습니다</p>
                            <p>댓글을 남겨보세요</p>
                        </div>}
                </div>
                <div className="absolute bottom-19 left-3 right-3 flex gap-2">
                    <img src={profileImg} alt="유저 이미지지" className="w-12 h-12 rounded-full border-2 border-gray-300 cursor-pointer" />
                    <div className="h-[50px] rounded-[25px] bg-[#D9D9D9] w-full px-5 flex justify-center items-center">
                        <input type="text" className="h-[50px] rounded-[25px] bg-[#D9D9D9] w-full px-1 focus:outline-none focus:ring-0" 
                            onKeyDown={(e)=>{
                                if (e.key === "Enter"){
                                    e.preventDefault()
                                    writeCommentHandler()
                                }}}
                            placeholder={!userId?"자신에게 댓글을 달 수 없습니다":""}
                            disabled={!userId}
                            ref={inputValue}/>
                        {emoji !== 9
                        ?<img src={getEmoji(emoji)} alt="이모지" className="w-[30px] h-[30px]" onClick={isOpenEmoji} /> 
                        : <img src={Sticker} alt="이모지" className="w-[30px] h-[30px]" onClick={isOpenEmoji} />}
                    </div>
                    {isEmoji?
                    <div className="absolute bottom-10 right-0 bg-white-300 rounded-full p-3 shadow-[6px_6px_5px_rgba(0,0,0,0.05)]">
                        <div>
                            <img src={KissingCat} alt="키스 이모지" className="w-[50px] h-[50px]" onClick={()=>{setEmoji(0),setIsEmoji(false)}}/>
                            <img src={GrinningCat} alt="기쁨 이모지" className="w-[50px] h-[50px]" onClick={()=>{setEmoji(1),setIsEmoji(false)}}/>
                        </div>
                        <div>
                            <img src={CryingCat} alt="우는 이모지" className="w-[50px] h-[50px]" onClick={()=>{setEmoji(2),setIsEmoji(false)}}/>
                            <img src={PoutingCat} alt="화남 이모지" className="w-[50px] h-[50px]" onClick={()=>{setEmoji(3),setIsEmoji(false)}}/>
                        </div>
                    </div>
                    :""}
                </div>
            </CustomModal>
        </div>
    )
}