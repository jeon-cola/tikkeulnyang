import axios from "axios"
import { useEffect, useState } from "react"
import Api from "../../../services/Api";
import MapCategory from "./category/MapCategory";
import AlertModal from "../../../components/AlertModal";
import ChooseAlertModal from "../../../components/ChooseAlertModal";
import CustomBackHeader from "../../../components/CustomBackHeader";
import Password from "../../../components/Password"

export default function List() {
  const [userData, setUserData] = useState([]);
  const [isAlertModal, setIsAlertModal] = useState(false)
  const [savingCheck, setSavingCheck] = useState(false)
  const [selectBucketListId, setSelectBucketListID] = useState(null)
  const [isDeleteModal, setIsDeleteModal] = useState(false)
  const [delteCheck, setDeleteCheck] = useState(false)
  const [isPassword, setIsPassword] = useState(false)
  const [checkMessage, setCheckMessage] = useState(false)


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

  // 저축 통신
  function saveHandler(password) {
    const fetchData = async () => {
      try {
        const response = await Api.post("api/bucket/saving",{"bucketId":selectBucketListId,"transactionPassword":password})
        if (response.data.status) {
          setIsAlertModal(true)
          refreshList()
        }
      } catch (error) {
        console.log(error)
      }
    }
    fetchData()
  }

    // 삭제 통신
    function deleteHandler(bucketListId) {
      console.log(bucketListId)
      const fetchData = async () => {
        try {
          const response = await Api.delete(`api/bucket/delete/${bucketListId}`)
          if (response.data.status) {
            setDeleteCheck(true)
            refreshList()
          }
        } catch (error) {
          console.log(error)
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
    <div className="flex flex-col gap-4 mb-4">
      <CustomBackHeader title="버킷 리스트 조회" showCreateButton={true} navigate="/bucketlist"/>
      <div className="mt-[37px] flex flex-col gap-3">
        {userData.map((data,index)=>(
          <MapCategory key={index} list={data} onSaving={checkAlertModalOpen} onDelete={deleteModalOpen}/>
        ))}
        <ChooseAlertModal title="저축하기" isClose={checkAlertModalClose} isOpen={savingCheck} isFunctionHandler={()=>saveHandler(selectBucketListId)}>
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

        <AlertModal title="저축 완료" isOpen={isAlertModal} isClose={AlertModalClose} height={170}>
          <div>
            <p>저축이 완료되었습니다다</p>
          </div>
        </AlertModal>

        <AlertModal title="삭제 완료" isClose={deleteCheckClose} isOpen={delteCheck} height={170}>
          <div>
            <p>삭제가 완료되었습니다</p>
          </div>
        </AlertModal>
      </div>
      {isPassword?<Password isFail={checkMessage} isFunction={saveHandler}/>:""}
    </div>
  )
}