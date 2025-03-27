import { useEffect, useRef, useState } from "react"
import Step from "../assets/Step"
import step2Image from "../assets/step2.png"
import { useNavigate } from "react-router-dom";
import Api from "../../../services/Api";
import BankImg from "../../mypage/assets/BankImgFunction";

export default function BucketListStep2() {
    const [stepCheck,setStepCheck] = useState({
        withdrawl_amount:"",
        withdrawl_amount_num:"",
        saving_account:"",
        saving_account_num:""
    });
    const [list, setList] = useState([]);
    const [withdrawalIsOpen, setWithdrawalIsOpen] = useState(false);
    const withdrawalDropdownRef = useRef(null);
    const [savingIsOpen, setSavingIsOpen] = useState(false);
    const savingDropdownRef = useRef(null);
    const nav = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
            const response = await Api.get("api/account/refresh")
            console.log(response.data)
            setList(response.data)
        }
        fetchData();
    },[])

    // 송금 계좌 선택
    function handleWithdrawalSelect(bank) {
        setStepCheck({
            ...stepCheck,
            withdrawl_amount:bank.bankName,
            withdrawl_amount_num: bank.accountNumber
        });
        setWithdrawalIsOpen(false);
    }

    // 저축 계좌 선택
    function handleSavingSelect(bank) {
        setStepCheck({
            ...stepCheck,
            saving_account:bank.bankName,
            saving_account_num: bank.accountNumber
        });
        setSavingIsOpen(false);
    }

    // 다음 페이지 이동 로직직
    function nextHandler(e) {
        e.preventDefault();
        const fetchData = async () => {
            try {
                const response = await Api.post("api/bucket/account", {
                    "saving_account": {
                        "bank_name":stepCheck.saving_account,
                        "account_number":stepCheck.saving_account_num
                    },
                    "withdrawal_account": {
                        "bank_name":stepCheck.withdrawl_amount,
                        "account_number":stepCheck.withdrawl_amount_num
                    }
                })
                console.log(response.data)
                if (response.data.status === "success") {
                    nav("/bucketlist/step3")
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData();
    } 

    // 체크 사항 확인
    const isChecked = 
        stepCheck.withdrawl_amount && 
        stepCheck.withdrawl_amount_num && 
        stepCheck.saving_account && 
        stepCheck.saving_account_num

    return(
        <div className="flex flex-col justify-center gap-4">
            
            {/* 진행 단계 */}
            <Step currentStep={2}/>
            <p className="w-full font-semibold text-xl">출근 통장과 저축통장을 선택해주세요</p>
            <img src={step2Image} alt="고양이 사진" className="w-full scale-[1] transform-gpu" />

            {/* 송금 계좌 선택 */}
            <div className="relative w-full" ref={withdrawalDropdownRef}>
                <div 
                    className="w-full p-2 border rounded flex items-center justify-between cursor-pointer"
                    onClick={() => setWithdrawalIsOpen(!withdrawalIsOpen)}
                >
                    {stepCheck.withdrawl_amount ? (
                        <div className="flex items-center justify-center w-full">
                            <span className="h-6 mr-2">
                                <BankImg bankName={stepCheck.withdrawl_amount} />
                            </span>
                            <span>{stepCheck.withdrawl_amount}</span>
                        </div>
                    ) : (
                        <span className="text-gray-400">송금 계좌를 선택해주세요</span>
                    )}
                    <span className="ml-2">▼</span>
                </div>
                
                {withdrawalIsOpen && (
                    <div className="absolute z-10 w-full mt-1 bg-white border rounded shadow-lg max-h-60 overflow-auto">
                        {list.map((bank) => (
                            <div 
                                key={bank.accountId}
                                className="p-2 hover:bg-gray-100 cursor-pointer flex items-center"
                                onClick={() => handleWithdrawalSelect(bank)}
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

            {/* 저축 계좌 선택 */}
            <div className="relative w-full" ref={savingDropdownRef}>
                <div 
                    className="w-full p-2 border rounded flex items-center justify-between cursor-pointer"
                    onClick={() => setSavingIsOpen(!savingIsOpen)}
                >
                    {stepCheck.saving_account ? (
                        <div className="flex items-center justify-center w-full">
                            <span className="h-6 mr-2">
                                <BankImg bankName={stepCheck.saving_account} />
                            </span>
                            <span>{stepCheck.saving_account}</span>
                        </div>
                    ) : (
                        <span className="text-gray-400">저축 계좌를 선택해주세요</span>
                    )}
                    <span className="ml-2">▼</span>
                </div>
                
                {savingIsOpen && (
                    <div className="absolute z-10 w-full mt-1 bg-white border rounded shadow-lg max-h-60 overflow-auto">
                        {list.map((bank) => (
                            <div 
                                key={bank.accountId}
                                className="p-2 hover:bg-gray-100 cursor-pointer flex items-center"
                                onClick={() => handleSavingSelect(bank)}
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

            {/* 다음 버튼 */}
            <div className="w-full mx-auto flex flex-col items-center">
                <button 
                    className="hover:bg-blue-600 disabled:opacity-50 disabled:bg-gray-400 disabled:cursor-not-allowed mx-auto" 
                     disabled={!isChecked} 
                     onClick={nextHandler}
                    >
                        다음
                </button>
            </div>
        </div>
    )
}
