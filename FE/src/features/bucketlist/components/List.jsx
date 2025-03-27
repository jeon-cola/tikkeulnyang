import axios from "axios"
import { useEffect, useState } from "react"
import Api from "../../../services/Api";
import MapCategory from "./category/MapCategory";

export default function List() {
  const [userData, setUserData] = useState([]);
  useEffect(()=> {
    const fetchData = async() => {
    try {
      const response = await Api.get("api/bucket/list")
        if (response.data.status === "success") {
          console.log(response.data.data.bucket_lists)
          setUserData(response.data.data.bucket_lists)
        }
      } catch (error) { 
        console.log(error)
      }
    }
    fetchData();
  },[])
  return (
    <div className="flex flex-col gap-4 mb-4">
      {userData.map((data,index)=>(
        <MapCategory key={index} list={data} />
      ))}
    </div>
  )
}