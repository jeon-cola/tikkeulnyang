import { useEffect, useState, useRef } from "react"
import CustomBackHeader from "../../../components/CustomBackHeader"
import bankImage from "../assets/bank.png"
import BankImg from "../assets/BankImgFunction"
import Api from "../../../services/Api";
import AlertModal from "../../../components/AlertModal";

export default function Account() {
    const [list, setList] = useState([])
    const [account, setAccount] = useState({
        bank:"",
        bankImformation:""
    });
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef(null);
    const [message, setMessage] = useState("")
    const [isAlertOpen, setIsAlertOpen] = useState(false) 

    function handleBankSelect(bank) {
        setAccount({
            bankImformation:bank,
            bank: bank.bankName
        });
        setIsOpen(false);
    }

    // 알렛창 닫기
    function closeHandler() {
        setIsAlertOpen(false)
    }

    //대표계좌 설정
    function registHandler(e) {
        e.preventDefault()
        const fetchData = async () => {
            console.log(account.bankImformation.accountNumber)
            const response = await Api.post(`/api/account/set-representative?accountNo=${account.bankImformation.accountNumber}`)
            console.log(response.data)
            setMessage("대표계좌로 설정되었습니다")
            setIsAlertOpen(true)
        }
        fetchData();
    }

    // 외부 클릭 감지
    useEffect(() => {
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // 계좌 불러오기기
    useEffect(() => {
        const fetchData = async()=>{
            try {
                const response = await Api.get("/api/account/refresh")    
                console.log(response.data)
                setList(response.data);
            } catch (error) {
                console.log(error)
            }
        } 
        fetchData()
    },[])

    return(
        <div className="flex flex-col justify-center gap-5 min-w-[345px]">
            <CustomBackHeader title="대표 계좌 설정"/>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                <p className="text-left font-regular text-lg">대표 계좌 선택</p>
                
                {/* 드롭다운 */}
                <div className="relative w-full" ref={dropdownRef}>
                    <div 
                        className="w-full p-2 border rounded flex items-center justify-between cursor-pointer"
                        onClick={() => setIsOpen(!isOpen)}
                    >
                        {account.bank ? (
                            <div className="flex items-center justify-center w-full">
                                <span className="h-6 mr-2">
                                    <BankImg bankName={account.bank} />
                                </span>
                                <span>{account.bank}</span>
                            </div>
                        ) : (
                            <span className="text-gray-400">대표 계좌를 선택해 주세요</span>
                        )}
                        <span className="ml-2">▼</span>
                    </div>
                    
                    {isOpen && (
                        <div className="absolute z-10 w-full mt-1 bg-white border rounded shadow-lg max-h-60 overflow-auto">
                            {list.map((bank) => (
                                <div 
                                    key={bank.accountId}
                                    className="p-2 hover:bg-gray-100 cursor-pointer flex items-center"
                                    onClick={() => handleBankSelect(bank)}
                                >
                                    <span className="h-6 mr-2">
                                        <BankImg bankName={bank.bankName} />
                                    </span>
                                    <span>{bank.bankName} : {bank.accountNumber}</span>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
            
            {/* 은행 이미지 */}
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4">
                <img src={bankImage} alt="은행 이미지" />
            </div>
            
            {/* 저장 버튼 */}
            <div className="w-full flex flex-col items-center">
                <button className="customButton bg-blue-500 text-white px-4 py-2 rounded" onClick={registHandler}>저장하기</button>
            </div>

            <AlertModal title="대표계좌 설정" isOpen={isAlertOpen} isClose={closeHandler} height={170}>
                {message}
            </AlertModal>
        </div>
    )
}