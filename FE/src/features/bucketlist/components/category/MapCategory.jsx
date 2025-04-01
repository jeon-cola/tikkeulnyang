import RealEstatePage  from "./RealEstatePage "
import VehiclePage from "./VehiclePage"
import WeddingFundPage  from "./WeddingFundPage "
import DigitalDevicePage  from "./DigitalDevicePage "
import TravelPage  from "./TravelPage "
import EducationPage  from "./EducationPage "
import HobbyPage  from "./HobbyPage "


export default function MapCategory({list, onSaving}) {
    const { category, title, bucket_id } = list;
    let { current_savings, target_amount } = list;

    console.log()
    current_savings = current_savings ?? 0;
    target_amount = target_amount ?? 0;

    if (category === "부동산") {
        return <RealEstatePage title={title} current_savings={current_savings} target_amount={target_amount} color="#B7D118" onSaving={onSaving} bucketListId={bucket_id}/>
    } else if (category === "자동차") {
        return <VehiclePage title={title} current_savings={current_savings} target_amount={target_amount} color="#EB4824" onSaving={onSaving} bucketListId={bucket_id}/>
    } else if (category === "여행") {
        return <TravelPage title={title} current_savings={current_savings} target_amount={target_amount} color="#A9E1FE" onSaving={onSaving} bucketListId={bucket_id}/>
    } else if (category === "결혼 자금") {
        return <WeddingFundPage title={title} current_savings={current_savings} target_amount={target_amount} color="#FC97B2" onSaving={onSaving} bucketListId={bucket_id}/>
    } else if (category === "디지털 기기") {
        return <DigitalDevicePage title={title} current_savings={current_savings} target_amount={target_amount} color="#7167A4" onSaving={onSaving} bucketListId={bucket_id}/>
    } else if (category === "교육비") {
        return <EducationPage title={list.title} current_savings={list.current_savings} target_amount={list.target_amount} color="#ED4C5C" onSaving={onSaving} bucketListId={bucket_id}/>
    } else if (category === "취미 활동") {
        return <HobbyPage title={title} current_savings={current_savings} target_amount={target_amount} color="#F0D5A8"  onSaving={onSaving} bucketListId={bucket_id}/>
    }
}