import { useDispatch, useSelector } from "react-redux"

export default function HomeMain() {
    const dispatch = useDispatch();
    const {nickName, email, profileImg} = useSelector(state => state.user)

    return (
        <>
        <p>
            {nickName}
        </p>
        <p>
            {email}
        </p>
        </>
    )
}