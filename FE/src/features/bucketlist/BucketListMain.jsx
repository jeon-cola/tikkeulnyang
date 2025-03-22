import { Route, Routes } from "react-router-dom";
import BucketListInformation from "./components/BucketListInformation";
import BucketListStep1 from "./components/BucketListStep1";
import BucketListStep2 from "./components/BucketListStep2";
import BucketListStep3 from "./components/BucketListStep3";
import List from "./components/List";

export default function BucketListMain() {
    return (
        <Routes>
            <Route path="/" element={<BucketListInformation/>}/>
            <Route path="step1" element={<BucketListStep1/>}/>
            <Route path="step2" element={<BucketListStep2/>}/>
            <Route path="step3" element={<BucketListStep3/>}/>
            <Route path="list" element={<List/>}/>
        </Routes>
    )
}
