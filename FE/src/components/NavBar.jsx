import classes from './NavBar.module.css';

export default function NavBar() {
    return(
    <>
        <nav className={classes.navBar}>
            <span>홈</span>
            <span>챌린지</span>
            <span>가계부</span>
            <span>카드 상품</span>
            <span>마이</span>
        </nav>
    </>
    )
}