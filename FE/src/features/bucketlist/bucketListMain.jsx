import { Route, Routes } from "react-router-dom";
import BucketListInformation from "./components/BucketListInformation";

export default function BucketListMain() {
    return (
        <Routes>
            <Route path="/" element={<BucketListInformation/>}/>
        </Routes>
    )
}