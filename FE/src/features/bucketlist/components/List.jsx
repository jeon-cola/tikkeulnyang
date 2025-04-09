import axios from "axios"
import { useEffect, useState } from "react"
import Api from "../../../services/Api";
import MapCategory from "./category/MapCategory";
import AlertModal from "../../../components/AlertModal";
import ChooseAlertModal from "../../../components/ChooseAlertModal";
import CustomBackHeader from "../../../components/CustomBackHeader";
import Password from "../../../components/Password"
import IsLoading from "../../../components/IsLoading"

export default function List() {
  const [userData, setUserData] = useState([]);
  const [isAlertModal, setIsAlertModal] = useState(false)
  const [savingCheck, setSavingCheck] = useState(false)
  const [selectBucketListId, setSelectBucketListID] = useState(null)
  const [isDeleteModal, setIsDeleteModal] = useState(false)
  const [delteCheck, setDeleteCheck] = useState(false)
  const [isPassword, setIsPassword] = useState(false)
  const [isMessage, setIsMessage] = useState("")
  const [checkMessage, setCheckMessage] = useState(false)
  const [isLoading, setIsLoading] = useState(false)


  // 버킷리스트 리스트 가져오기
  useEffect(()=> {
    const fetchData = async() => {
    try {
      const response = await Api.get("api/bucket/list")
        if (response.data.status === "success") {
          setUserData(response.data.data.bucket_lists)
        }
      } catch (error) { 
        console.log(error)
      }
    }
    fetchData();
  },[])

  //비밀번호 
  function passwordHandler() {
    setIsPassword(true)
  }

  // 저축 통신
  function saveHandler(password) {
    setIsLoading(true)
    const fetchData = async () => {
      try {
        const response = await Api.post("api/bucket/saving",{"bucketId":selectBucketListId,"transactionPassword":password})
        if (response.data.status) {
          setIsMessage("저축이 완료되었습니다")
          setIsPassword(false)
          setIsAlertModal(true)
          refreshList()
        }
      } catch (error) {
        if (error.response.data === "계좌잔액이 부족합니다.") {
          setIsMessage("계좌잔액이 부족합니다")
        } else if (error.response.data === "거래 비밀번호가 올바르지 않습니다.") {
          setCheckMessage(true)
        }
      } finally {
        setIsLoading(false)
      }
    }
    fetchData()
  }

    // 삭제 통신
    function deleteHandler(bucketListId) {
      setIsLoading(true)
      const fetchData = async () => {
        try {
          const response = await Api.delete(`api/bucket/delete/${bucketListId}`)
          if (response.data.status) {
            setIsMessage(response.data.message)
            setDeleteCheck(true)
            refreshList()
          }
        } catch (error) {
          setIsMessage(error.response.data)
        } finally {
          setIsLoading(false)
        }
      }
      fetchData()
    }
    // 삭제 확인 닫기
    function deleteCheckClose() {
      setDeleteCheck(false)
    }

  // 삭제모달 열기
  function deleteModalOpen(bucketListId) {
    setSelectBucketListID(bucketListId)
    setIsDeleteModal(true)
  }

  // 삭제 모달 닫기
  function deleteModalClose() {
    setIsDeleteModal(false)
  }

  // 리스트 새로고침
  function refreshList() {
    const fetchData = async() => {
      try {
        const response = await Api.get("api/bucket/list")
          if (response.data.status === "success") {
            setUserData(response.data.data.bucket_lists)
          }
        } catch (error) { 
          console.log(error)
        }
      }
      fetchData();
  }


  //체크 모달 열기
  function checkAlertModalOpen(bucketListId) {
    setSelectBucketListID(bucketListId)
    setSavingCheck(true)
  }

  // 체크 모달 닫기
  function checkAlertModalClose (){
    setSavingCheck(false)
  }

  //모달 닫기
  function AlertModalClose() {
    setIsAlertModal(false)
  }


  return (
    <>
    {!isLoading?

<div className="flex flex-col gap-4 mb-4">
      <CustomBackHeader title="버킷 리스트 조회" showCreateButton={true} navigate="/bucketlist"/>
      <div className="mt-[37px] flex flex-col gap-3">
      {userData.map((data,index)=>(
        <MapCategory key={index} list={data} onSaving={checkAlertModalOpen} onDelete={deleteModalOpen}/>
      ))}
        <ChooseAlertModal title="저축하기" isClose={checkAlertModalClose} isOpen={savingCheck} isFunctionHandler={passwordHandler}>
        <div>
        <p>확인 버튼을 누르면 자동으로</p>
        <p>설정된 계좌를 통해 저축이 이루어 집니다</p>
        <p>저축을 진행하시겠습니까?</p>
          </div>
          </ChooseAlertModal>
          
          <ChooseAlertModal title="삭제하기" isClose={deleteModalClose} isOpen={isDeleteModal} isFunctionHandler={()=>deleteHandler(selectBucketListId)}>
          <div>
          <p>확인 버튼을 누르면</p>
          <p>해당 버킷리스트가 삭제됩니다</p>
          <p>정말 삭제하시겠습니까?</p>
          </div>
          </ChooseAlertModal>
          
          <AlertModal title="저축 송금" isOpen={isAlertModal} isClose={AlertModalClose} height={170}>
          <div>
          <p>{isMessage}</p>
          </div>
          </AlertModal>
          
          <AlertModal title="삭제 완료" isClose={deleteCheckClose} isOpen={delteCheck} height={170}>
          <div>
          <p>{isMessage}</p>
          </div>
          </AlertModal>
          </div>
          {isPassword?<Password isFail={checkMessage} isFunction={saveHandler}/>:""}
          </div>
      :<IsLoading/>}
      </>
        )
      }