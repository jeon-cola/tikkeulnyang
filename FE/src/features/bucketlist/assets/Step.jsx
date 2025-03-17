export default function Step({currentStep}){
    
    return(
    <div className="flex justify-center w-full">
        <svg width="249" height="45" viewBox="0 0 249 45" fill="none" xmlns="http://www.w3.org/2000/svg">
            <line x1="20.5" y1="25.5" x2="214.5" y2="25.5" stroke="#D9D9D9" strokeWidth="5" strokeLinecap="round" strokeDasharray="1 12"/>
            
            {/* 첫번째 발자국 */}
            <path d="M8.96554 10.3873C-3.23348 9.36431 -1.27794 24.6063 5.4614 27.6865C4.72336 29.6099 5.77891 37.3314 9.09729 38.0764C18.1539 40.1095 24.6279 40.1134 33.6897 37.5583C38.3062 35.0157 38.0277 29.9769 37.5667 27.3189C45.6109 21.9726 46.1666 8.48943 34.9674 10.3001C33.1298 0.997548 26.4115 2.34989 21.9432 5.46776C16.9907 1.05166 10.2746 2.84727 8.96554 10.3873Z" fill={currentStep == 1 ? "#FFEBB8" : "#FFFCFC"}/>
            <ellipse cx="6.2062" cy="4.93176" rx="6.2062" ry="4.93176" transform="matrix(0.00475814 0.999989 -0.999994 0.00335332 20.6332 5.20703)" fill={currentStep == 1 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.21617" cy="4.92401" rx="6.21617" ry="4.92401" transform="matrix(0.38289 0.924327 -0.926781 0.374311 9.18408 11.4512)" fill={currentStep == 1 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.21617" cy="4.924" rx="6.21617" ry="4.924" transform="matrix(-0.374082 0.926865 0.930313 0.368084 34.3339 11.3667)" fill={currentStep == 1 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.2062" cy="4.93176" rx="6.2062" ry="4.93176" transform="matrix(0.00475814 0.999989 -0.999994 0.00335332 32.2892 5.16748)" fill={currentStep == 1 ? "#FF957A" : "#D9D9D9"}/>
            <rect width="27.7952" height="11.525" rx="5.76249" transform="matrix(0.999994 -0.00335332 0.00475814 0.999989 7.28113 25.6426)" fill={currentStep == 1 ? "#FF957A" : "#D9D9D9"}/>
            <rect width="17.7307" height="11.656" rx="5.82802" transform="matrix(-0.00475814 -0.999989 -0.999994 0.00335332 27.0616 37.1016)" fill={currentStep == 1 ? "#FF957A" : "#D9D9D9"}/>
            <path d="M22.7068 25.3945V36H21.0808V27.0059H21.0222L18.488 28.6611V27.123L21.1248 25.3945H22.7068Z" fill="white"/>
            
            {/* 두번째 발자국 */}
            <g filter="url(#filter0_d_754_1186)">
            <path d="M108.966 10.3868C96.7665 9.36383 98.7221 24.6058 105.461 27.6861C104.723 29.6094 105.779 37.3309 109.097 38.0759C118.154 40.109 124.628 40.1129 133.69 37.5579C138.306 35.0152 138.028 29.9765 137.567 27.3184C145.611 21.9722 146.167 8.48894 134.967 10.2996C133.13 0.99706 126.412 2.3494 121.943 5.46727C116.991 1.05118 110.275 2.84678 108.966 10.3868Z" fill={currentStep >= 2 ? "#FFEBB8" : "#FFFCFC"}/>
            <ellipse cx="6.2062" cy="4.93176" rx="6.2062" ry="4.93176" transform="matrix(0.00475814 0.999989 -0.999994 0.00335332 120.633 5.20654)" fill={currentStep == 2 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.21617" cy="4.92401" rx="6.21617" ry="4.92401" transform="matrix(0.38289 0.924327 -0.926781 0.374311 109.184 11.4507)" fill={currentStep == 2 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.21617" cy="4.924" rx="6.21617" ry="4.924" transform="matrix(-0.374082 0.926865 0.930313 0.368084 134.334 11.3662)" fill={currentStep == 2 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.2062" cy="4.93176" rx="6.2062" ry="4.93176" transform="matrix(0.00475814 0.999989 -0.999994 0.00335332 132.289 5.16699)" fill={currentStep == 2 ? "#FF957A" : "#D9D9D9"}/>
            <rect width="27.7952" height="11.525" rx="5.76249" transform="matrix(0.999994 -0.00335332 0.00475814 0.999989 107.281 25.6421)" fill={currentStep == 2 ? "#FF957A" : "#D9D9D9"}/>
            <rect width="17.7307" height="11.656" rx="5.82802" transform="matrix(-0.00475814 -0.999989 -0.999994 0.00335332 127.062 37.1011)" fill={currentStep == 2 ? "#FF957A" : "#D9D9D9"}/>
            <path d="M116.955 36V34.8281L120.632 31.1221C121.789 29.9062 122.375 29.2324 122.375 28.3096C122.375 27.2695 121.526 26.5957 120.412 26.5957C119.241 26.5957 118.479 27.3428 118.479 28.4707H116.955C116.941 26.5371 118.42 25.248 120.442 25.248C122.493 25.248 123.899 26.5371 123.913 28.2656C123.899 29.4521 123.342 30.3896 121.35 32.3525L119.197 34.5352V34.623H124.089V36H116.955Z" fill="white"/>
            </g>
            
            {/* 세번째 발자국 */}
            <g filter="url(#filter1_d_754_1186)">    
            <path d="M208.966 10.3868C196.767 9.36383 198.722 24.6058 205.461 27.6861C204.723 29.6094 205.779 37.3309 209.097 38.0759C218.154 40.109 224.628 40.1129 233.69 37.5579C238.306 35.0152 238.028 29.9765 237.567 27.3184C245.611 21.9722 246.167 8.48894 234.967 10.2996C233.13 0.99706 226.412 2.3494 221.943 5.46727C216.991 1.05118 210.275 2.84678 208.966 10.3868Z" fill={currentStep == 3 ? "#FFEBB8" : "#FFFCFC"}/>
            <ellipse cx="6.2062" cy="4.93176" rx="6.2062" ry="4.93176" transform="matrix(0.00475814 0.999989 -0.999994 0.00335332 220.633 5.20654)" fill={currentStep == 3 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.21617" cy="4.92401" rx="6.21617" ry="4.92401" transform="matrix(0.38289 0.924327 -0.926781 0.374311 209.184 11.4507)" fill={currentStep == 3 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.21617" cy="4.924" rx="6.21617" ry="4.924" transform="matrix(-0.374082 0.926865 0.930313 0.368084 234.334 11.3662)" fill={currentStep == 3 ? "#FF957A" : "#D9D9D9"}/>
            <ellipse cx="6.2062" cy="4.93176" rx="6.2062" ry="4.93176" transform="matrix(0.00475814 0.999989 -0.999994 0.00335332 232.289 5.16699)" fill={currentStep == 3 ? "#FF957A" : "#D9D9D9"}/>
            <rect width="27.7952" height="11.525" rx="5.76249" transform="matrix(0.999994 -0.00335332 0.00475814 0.999989 207.281 25.6421)" fill={currentStep == 3 ? "#FF957A" : "#D9D9D9"}/>
            <rect width="17.7307" height="11.656" rx="5.82802" transform="matrix(-0.00475814 -0.999989 -0.999994 0.00335332 227.062 37.1011)" fill={currentStep == 3 ? "#FF957A" : "#D9D9D9"}/>
            <path d="M220.5 36.1465C218.303 36.1465 216.736 34.96 216.677 33.2314H218.318C218.376 34.1689 219.299 34.7549 220.486 34.7549C221.76 34.7549 222.698 34.0518 222.698 33.0264C222.698 31.9863 221.819 31.2246 220.31 31.2246H219.402V29.9062H220.31C221.511 29.9062 222.361 29.2324 222.361 28.2217C222.361 27.2549 221.643 26.5957 220.515 26.5957C219.446 26.5957 218.508 27.1816 218.464 28.1484H216.911C216.955 26.4199 218.552 25.248 220.53 25.248C222.61 25.248 223.928 26.5664 223.913 28.1338C223.928 29.335 223.181 30.2139 222.038 30.4922V30.5654C223.489 30.7705 224.324 31.7373 224.324 33.085C224.324 34.8574 222.698 36.1465 220.5 36.1465Z" fill="white"/>
            </g>
            <defs>
            <filter id="filter0_d_754_1186" x="97.0996" y="0.0913086" width="51.4042" height="44.4517" filterUnits="userSpaceOnUse" colorInterpolationFilters="sRGB">
            <feFlood floodOpacity="0" result="BackgroundImageFix"/>
            <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/>
            <feOffset dx="1" dy="1"/>
            <feGaussianBlur stdDeviation="2"/>
            <feComposite in2="hardAlpha" operator="out"/>
            <feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/>
            <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_754_1186"/>
            <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_754_1186" result="shape"/>
            </filter>
            <filter id="filter1_d_754_1186" x="197.1" y="0.0913086" width="51.4042" height="44.4517" filterUnits="userSpaceOnUse" colorInterpolationFilters="sRGB">
            <feFlood floodOpacity="0" result="BackgroundImageFix"/>
            <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/>
            <feOffset dx="1" dy="1"/>
            <feGaussianBlur stdDeviation="2"/>
            <feComposite in2="hardAlpha" operator="out"/>
            <feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"/>
            <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_754_1186"/>
            <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_754_1186" result="shape"/>
            </filter>
            </defs>
        </svg>
    </div>
    )
}