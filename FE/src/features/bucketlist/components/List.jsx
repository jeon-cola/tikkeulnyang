import axios from "axios"
import { useEffect, useState } from "react"

export default function List() {
  const [userData, setUserData] = useState([]);
  useEffect(()=> {
    const fetchData = async() => {
    try {
        const response = await axios.get("http://localhost:3000/bucket_lists")
        setUserData(response.data)
        console.log(response.data)
      } catch (error) { 
        console.log(error)
      }
    }
    fetchData();
  },[])
  return (
    <div>
      {userData.map((data)=>(
        <p>{data.title}</p>
      ))}
    </div>
  )
}