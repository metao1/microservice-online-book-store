import React, {PureComponent} from 'react';
//Internals
import './index.css';

class Icon extends PureComponent {
    render() {
        let outlet = <span/>;
        switch (this.props.icon) {
            case "dollar":
                outlet = <i className="icon fas fa-dollar-sign"></i>;
                break;
            case "music":
                outlet = <i className="icon fas fa-music"></i>;
                break;
            case "camera":
                outlet = <i className="icon fas fa-plug"></i>;
                break;
            case "admin":
                outlet =
                    <svg className="icon" width="24px" height="24px" viewBox="0 0 24 24" version="1.1"
                         xmlns="http://www.w3.org/2000/svg">
                        <g id="Page-1" stroke="none" fill={this.props.color || "#000000"} fillRule="evenodd">
                            <path
                                d="M24.929494,14.3242344 C24.8663657,14.6563863 24.8255751,14.9946081 24.7366128,15.3196216 C24.0051018,17.9911615 22.2991331,19.7258779 19.5971945,20.307872 C16.9195845,20.8845731 14.6765884,20.0041765 12.9214768,17.8962749 C12.7516132,17.6921763 12.6684781,17.4778316 12.8041068,17.2300774 C12.940221,16.9812549 13.1639379,16.9503706 13.4290281,16.973728 C14.9999509,17.1119304 16.5697082,17.2855817 18.1439817,17.3562368 C19.8179492,17.4312623 21.2922372,16.2694108 21.7117003,14.629581 C22.2900038,12.3692004 20.5742259,10.128341 18.2399852,10.1203771 C17.6033608,10.1181919 16.9662509,10.2115246 16.329578,10.2643096 C15.3220993,10.3477846 14.3150091,10.4349987 13.3075305,10.518328 C13.0852218,10.5366838 12.9100166,10.4469931 12.8031356,10.2506156 C12.6852314,10.0339399 12.7299068,9.83251217 12.8779668,9.64215615 C13.7523905,8.51764737 14.8620399,7.72859253 16.2232797,7.30315653 C20.0948395,6.09318179 24.2080835,8.6523534 24.8403374,12.6611929 C24.86656,12.8276088 24.8996295,12.9929563 24.929494,13.1587894 L24.929494,14.3242344 Z M1.38659932,7.38012446 C1.52072262,7.09900941 1.74507079,6.99946098 2.05279684,6.99999514 C5.08747002,7.00538533 6.80221054,7.00198611 9.83688372,7.00344292 C10.7104819,7.00387996 11.3634225,7.58111517 11.4579206,8.42713114 C11.5413471,9.17374437 11.0134005,9.92045471 10.2476545,10.0694374 C9.91113228,10.1348966 9.55513738,10.1037695 9.20788332,10.1103737 C8.93487782,10.1155696 8.66172665,10.1113935 8.38197128,10.1113935 C8.37177363,10.1751044 8.36041054,10.2134185 8.36031342,10.2518296 C8.35919654,10.8089123 8.3594879,11.3660921 8.3594879,11.9495431 C8.4377184,11.9495431 8.49890426,11.9539135 8.55931316,11.9489118 C10.0506943,11.8259088 11.5419784,11.7019346 13.0332624,11.5781546 C14.2908261,11.4737987 15.5482928,11.3692486 16.8059051,11.2652812 C17.2251254,11.2306092 17.6438601,11.1859338 18.0637602,11.1657327 C19.4771051,11.0977484 20.6236602,12.108335 20.779927,13.5499905 C20.9192462,14.8357677 19.936776,16.1031406 18.5958344,16.2898546 C18.1311618,16.3545368 17.6441514,16.2864068 17.1696696,16.248627 C15.2829598,16.0983331 13.397124,15.936482 11.5109969,15.77803 C10.6324456,15.704267 9.75408856,15.6277361 8.87553725,15.5541189 C8.71557992,15.5407162 8.55503986,15.5353261 8.37643541,15.5252255 L8.37643541,17.3722617 C8.90481905,17.3722617 9.4208684,17.3619183 9.93628647,17.3747868 C10.5998132,17.3913944 11.0769658,17.7171363 11.3362773,18.3225364 C11.7826913,19.36459 11.0204903,20.4749677 9.86111526,20.4782698 C7.92701067,20.4838542 5.99290609,20.4802608 4.0588015,20.4802608 C2.95823291,20.4802608 3.17749985,20.4735595 2.07702838,20.48366 C1.76230967,20.486525 1.52654985,20.393775 1.38659932,20.1028994 L1.38659932,17.7234491 C1.53995247,17.4447621 1.77512956,17.3643464 2.08926556,17.3659974 C4.16783677,17.3768263 4.92657244,17.3716304 7.00524078,17.3716304 L7.30111813,17.3716304 L7.30111813,15.4333982 C6.9418697,15.3972694 5.04405719,14.6180723 3.79873062,14.2238119 C3.57889855,14.1541766 3.46259685,13.9780487 3.46439357,13.7402979 C3.46614174,13.5034698 3.58482289,13.3390935 3.80295535,13.2584835 C4.06789986,13.1605376 6.93715936,12.0865314 7.30131237,12.0478289 L7.30131237,10.1113935 L6.95692337,10.1113935 C4.88636459,10.111442 4.13568991,10.1063918 2.06522825,10.1169293 C1.75662812,10.1185318 1.53519357,10.0265588 1.38659932,9.75957474 L1.38659932,7.38012446 Z"
                                id="Combined-Shape" transform="rotate(-45.000000) translate(-11.158047, 3) "></path>
                        </g>
                    </svg>;
                break;
            case "art":
                outlet = <svg className="icon" width="18px" height="18px" viewBox="0 0 18 18" version="1.1"
                              xmlns="http://www.w3.org/2000/svg">
                    <g id="art-icon" stroke="none" fill={this.props.color || "#000000"} fillRule="evenodd">
                        <path
                            d="M9,0 C4.03,0 0,4.03 0,9 C0,13.97 4.03,18 9,18 C9.83,18 10.5,17.33 10.5,16.5 C10.5,16.11 10.35,15.76 10.11,15.49 C9.88,15.23 9.73,14.88 9.73,14.5 C9.73,13.67 10.4,13 11.23,13 L13,13 C15.76,13 18,10.76 18,8 C18,3.58 13.97,0 9,0 Z M3.5,9 C2.67,9 2,8.33 2,7.5 C2,6.67 2.67,6 3.5,6 C4.33,6 5,6.67 5,7.5 C5,8.33 4.33,9 3.5,9 Z M6.5,5 C5.67,5 5,4.33 5,3.5 C5,2.67 5.67,2 6.5,2 C7.33,2 8,2.67 8,3.5 C8,4.33 7.33,5 6.5,5 Z M11.5,5 C10.67,5 10,4.33 10,3.5 C10,2.67 10.67,2 11.5,2 C12.33,2 13,2.67 13,3.5 C13,4.33 12.33,5 11.5,5 Z M14.5,9 C13.67,9 13,8.33 13,7.5 C13,6.67 13.67,6 14.5,6 C15.33,6 16,6.67 16,7.5 C16,8.33 15.33,9 14.5,9 Z"
                            id="Shape"></path>
                    </g>
                </svg>;
                break;
            case "makeup":
                outlet =
                    <svg width="19px" height="21px" viewBox="0 0 19 21" version="1.1"
                         xmlns="http://www.w3.org/2000/svg">
                        <g id="Combined-Shape" stroke="none" fill={this.props.color || "#000000"} fillRule="evenodd">
                            <path
                                d="M15.799752,0.372702953 C15.8575251,0.450318196 15.9529403,0.616416737 16.0082593,0.742241341 C16.1457339,1.06204211 16.7469094,4.41993077 16.7230289,4.73851945 L16.7045924,5.00503508 L15.5998576,6.60958257 L14.4980552,8.20994231 L12.6733005,6.91975339 L10.8514781,5.62537671 L12.6603037,3.04210606 C14.6033483,0.267150681 14.5945515,0.279713931 15.0131124,0.144436346 C15.3233365,0.0391132487 15.5944187,0.123398019 15.799752,0.372702953 Z M14.478456,9.43845442 C14.6399128,9.6882415 14.6516419,9.6714905 13.2250809,11.7088308 L11.903416,13.5963639 L12.1207149,13.7485183 C12.3798314,13.9299536 12.5368254,14.1950551 12.5099618,14.393544 C12.4934002,14.5061993 12.0056534,15.2202494 10.2673891,17.6852726 L8.04470551,20.8417119 L7.82220432,20.8660518 L7.60371304,20.8931995 L1.25087026,16.4448911 L1.20184327,16.2304251 L1.14861542,16.0130176 L3.35449558,12.8448124 C5.07614737,10.368157 5.58050664,9.66573919 5.68070358,9.61164592 C5.85803427,9.51851518 6.16084326,9.57537124 6.41575895,9.75386513 L6.63725873,9.90896095 L9.28925214,6.12152184 L9.65315638,6.02853841 L12.0372794,7.68543737 C13.3440628,8.59449973 14.4469657,9.38548353 14.478456,9.43845442 Z"
                                fillRule="nonzero"></path>
                        </g>
                    </svg>;
                break;
            case "cookbook":
                outlet = <svg className="icon" width="23px" height="18px" viewBox="0 0 23 18" version="1.1"
                              xmlns="http://www.w3.org/2000/svg">
                    <g id="cookbook-icon" stroke="none" fill={this.props.color || "#000000"} fillRule="evenodd">
                        <path
                            d="M20.8979008,8.65624585 C21.5066647,8.65624585 21.999956,9.21770861 22,9.90965674 C22,10.601705 21.5067087,11.1631177 20.8979448,11.1631177 L18.9693482,11.1631177 L18.9693482,14.4836573 C18.9693482,16.4258105 17.5853053,18 15.8777011,18 L7.12229888,18 C5.41469466,18 4.03065179,16.4258105 4.03065179,14.4836573 L4.03065179,11.1631177 L2.1020552,11.1631177 C1.49329125,11.1631177 1,10.601655 1,9.90965674 C1,9.21765852 1.49324721,8.65624585 2.1020552,8.65624585 L4.03065179,8.65624585 L4.03065179,7.21629849 C4.03065179,6.73933815 4.37037581,6.35294118 4.78972402,6.35294118 L18.2102319,6.35294118 C18.6295802,6.35294118 18.9693042,6.73933815 18.9693042,7.21629849 L18.9693042,8.65624585 L20.8979008,8.65624585 Z M1.38790455,6.32530542 C1.30326583,6.34423524 1.21826405,6.35294118 1.13471452,6.35294118 C0.616444186,6.35294118 0.148412493,6.00238226 0.0286475715,5.48377651 C-0.110722673,4.88226349 0.271898796,4.28365245 0.883702854,4.14618799 L6.36870028,2.91686557 L5.55721238,1.00763207 C5.50134629,0.876685907 5.50724604,0.727970708 5.573414,0.601756998 C5.63958196,0.475587934 5.75902921,0.384287249 5.8998517,0.352990535 L7.41958187,0.0118518873 C7.56044974,-0.0194001809 7.70830655,0.0118518873 7.82326091,0.0973486168 C7.93862371,0.183202513 8.00955686,0.314461199 8.01699962,0.456300943 L8.1263719,2.52295557 L13.6120501,1.29349921 C14.2212673,1.15674908 14.8323452,1.53280076 14.9713524,2.13502811 C15.1107227,2.73654113 14.7281012,3.33515218 14.1162971,3.47261663 L1.38790455,6.32530542 Z M17.3960004,5.29263073 C17.2479891,5.30519606 17.1063878,5.2372589 17.0392159,5.12159227 C16.971885,5.0047535 16.9925981,4.8630185 17.0910251,4.76484017 C17.6290361,4.23015744 17.9043985,3.4720651 18.0355639,2.97367177 C17.8909959,2.85411364 17.7580824,2.7193177 17.6566889,2.55775012 C17.1336697,1.72745404 17.4703239,0.679093298 18.4083997,0.216192332 C19.077629,-0.113788119 19.8690179,-0.051149034 20.4589441,0.31015096 C20.5275463,0.261249339 20.5987443,0.214645109 20.6777826,0.175308139 C21.4316124,-0.19668176 22.3826141,0.0427627086 22.8029153,0.709615787 C23.1744798,1.30004543 23.0031596,2.0272402 22.4361715,2.44353694 C22.4586328,2.4744814 22.4862326,2.5004091 22.5065219,2.5325257 C23.0070797,3.32765762 22.6846758,4.33175841 21.7867018,4.77515499 C21.0872239,5.11929488 20.2505417,5.01919424 19.6748657,4.58569051 C19.1005141,5.03757338 18.164981,5.2223493 17.3960004,5.29263073 Z"
                            id="Combined-Shape"></path>
                    </g>
                </svg>;
                break;
            case "book":
                outlet = <svg className="icon" width="23px" height="18px" viewBox="0 0 23 22" version="1.1"
                              xmlns="http://www.w3.org/2000/svg">
                    <g stroke="none" fillRule="evenodd" id="book-icon" transform="translate(-274, -10)"
                       fill={this.props.color || "#000000"}>
                        <path
                            d="M291.22895,16.9998911 C291.428672,17.0015981 291.629094,16.9568121 291.821666,16.8651988 C291.987547,16.7862558 292.097702,16.6452636 292.209271,16.5023181 C292.442004,16.2041438 292.681455,15.9117768 292.911377,15.6111916 C293.05416,15.4245336 293.022463,15.2198027 292.830374,15.0935397 C292.738262,15.0330039 292.632031,14.9879715 292.526482,14.9602553 C291.692519,14.7412351 290.857093,14.5283918 290.022248,14.3130496 C288.449111,13.907301 286.875825,13.5021859 285.303253,13.0940793 C285.046324,13.0274019 284.791141,12.9680802 284.524667,13.0194301 C284.298619,13.0629843 284.071707,13.1192967 283.914988,13.3076617 C283.629756,13.6504636 283.357078,14.0051438 283.084385,14.3593666 C282.960443,14.5203497 282.972633,14.7226344 283.123597,14.8535432 C283.212284,14.9304449 283.324118,14.9927229 283.434972,15.0232548 C284.015099,15.1830413 284.598054,15.3314245 285.180376,15.482201 C286.941146,15.9380852 288.701833,16.394251 290.463202,16.8475132 C290.712082,16.9115686 290.956771,17.0036042 291.22895,16.9998911 M274,24.9019338 L274,24.3559287 C274.004864,24.3237599 274.010709,24.2916973 274.01443,24.2593768 C274.032845,24.0990637 274.038661,23.9359598 274.071476,23.7789227 C274.125445,23.5207229 274.202078,23.2676951 274.259592,23.010193 C274.355855,22.579122 274.48971,22.1618224 274.668406,21.7602357 C274.800122,21.4642251 274.936203,21.168078 275.145971,20.9218752 C275.635051,20.3478267 276.131763,19.7807094 276.625472,19.2108772 C277.471139,18.2348021 278.31688,17.2587877 279.162577,16.2827429 C280.055548,15.2521432 280.948534,14.2215434 281.841491,13.1909284 C282.441117,12.4988518 283.040626,11.8066539 283.640369,11.1146682 C283.876698,10.8419994 284.110697,10.5670858 284.350879,10.2981025 C284.59005,10.0302566 284.903948,9.9554236 285.229362,10.0239017 C286.02377,10.1911158 286.812802,10.3857363 287.604134,10.568739 C288.775713,10.8396485 289.947468,11.1097087 291.119017,11.3807699 C292.067203,11.6001274 293.015125,11.8206225 293.963253,12.0402379 C294.72842,12.2174924 295.494555,12.3905153 296.258609,12.57279 C296.595962,12.6532803 296.829375,12.8687854 296.946763,13.2093713 C296.971843,13.2821416 296.982625,13.36019 297,13.4358269 L297,13.587495 C296.995898,13.5963827 296.989101,13.6049671 296.98809,13.6142189 C296.964475,13.8303156 296.871625,14.0113466 296.734401,14.1732674 C296.470458,14.4846723 296.209943,14.7991864 295.947816,15.1122597 C295.260585,15.9330722 294.573368,16.7538998 293.885903,17.5745 C293.366293,18.1947466 292.846068,18.8144169 292.326576,19.4347697 C291.795906,20.0684692 291.265997,20.7028513 290.735445,21.3366267 C290.08661,22.1116658 289.43738,22.8863257 288.788443,23.6612586 C288.305707,24.2377035 287.827029,24.8178794 287.33896,25.3894254 C287.080217,25.6924278 286.747962,25.7929838 286.370499,25.6855725 C285.837822,25.5339954 285.308265,25.3707095 284.777317,25.2126107 C283.631023,24.8712514 282.4847,24.5299678 281.338362,24.1887602 C280.105562,23.8217992 278.872821,23.4545501 277.639787,23.0883476 C277.357531,23.0045206 277.080432,22.8960931 276.775309,22.9432467 C276.548122,22.9783579 276.336654,23.0392223 276.172328,23.2168256 C275.976081,23.4289486 275.881971,23.6912889 275.830624,23.9732702 C275.760818,24.3564747 275.759207,24.7423183 275.804108,25.1280406 C275.848277,25.5073928 275.947968,25.8698492 276.144508,26.1975129 C276.361353,26.5589986 276.652501,26.8284066 277.051485,26.9559898 C277.337476,27.0474457 277.625269,27.1328803 277.912329,27.2207416 C279.761031,27.7865849 281.609704,28.3525647 283.458494,28.9181046 C284.623392,29.2744488 285.788525,29.6298677 286.953234,29.9868489 C286.992143,29.99877 287.017634,30.0016972 287.047636,29.9641745 C287.570381,29.3104396 288.094971,28.658282 288.618683,28.0053661 C289.354331,27.088214 290.089408,26.1705766 290.825057,25.2534246 C291.501506,24.4100742 292.178555,23.5672395 292.855048,22.7238892 C293.56736,21.8359181 294.279627,20.9479014 294.989888,20.0581558 C295.010456,20.0323722 295.022703,19.9828374 295.014338,19.951336 C294.78091,19.072738 294.872002,18.2200145 295.213105,17.3916338 C295.459161,16.7940464 296.20943,16.6488545 296.642269,17.1130347 C296.924144,17.4153092 296.96279,17.7692873 296.819648,18.1575879 C296.607403,18.7333048 296.609659,19.311236 296.827544,19.8863614 C296.928759,20.1535399 296.928275,20.4224777 296.782379,20.6653286 C296.663424,20.863301 296.511917,21.0412228 296.3675,21.2217382 C295.617392,22.1593199 294.864633,23.0946115 294.113397,24.0312074 C293.454132,24.8531574 292.795878,25.6759719 292.136789,26.4980432 C291.50571,27.2851702 290.873957,28.0717512 290.242776,28.8587872 C289.493137,29.7935176 288.743908,30.728612 287.994138,31.6632514 C287.897274,31.7839792 287.779915,31.878438 287.635382,31.9283672 C287.546018,31.9592316 287.452275,31.9765824 287.36051,32 L287.257962,32 C287.170606,31.9789636 287.081946,31.9625228 286.996113,31.9361932 C285.787236,31.5653193 284.578857,31.1927466 283.370141,30.8212963 C281.174359,30.1464795 278.978343,29.4724514 276.78278,28.7968611 C276.629149,28.7495862 276.46979,28.7099098 276.32801,28.6360323 C275.851969,28.3879792 275.447008,28.0410991 275.093027,27.6305336 C274.540295,26.9894478 274.190138,26.2466534 274.059346,25.3962959 C274.034134,25.2324034 274.019513,25.0667667 274,24.9019338"
                            id="Fill-1"></path>
                    </g>
                </svg>;
                break;
            case "scifi":
                outlet = <svg className="icon" width="24px" height="24px" viewBox="0 0 24 24" version="1.1"
                              xmlns="http://www.w3.org/2000/svg">
                    <g id="Page-1" stroke="none" fill={this.props.color || "#000000"} fillRule="evenodd">
                        <path
                            d="M24.929494,14.3242344 C24.8663657,14.6563863 24.8255751,14.9946081 24.7366128,15.3196216 C24.0051018,17.9911615 22.2991331,19.7258779 19.5971945,20.307872 C16.9195845,20.8845731 14.6765884,20.0041765 12.9214768,17.8962749 C12.7516132,17.6921763 12.6684781,17.4778316 12.8041068,17.2300774 C12.940221,16.9812549 13.1639379,16.9503706 13.4290281,16.973728 C14.9999509,17.1119304 16.5697082,17.2855817 18.1439817,17.3562368 C19.8179492,17.4312623 21.2922372,16.2694108 21.7117003,14.629581 C22.2900038,12.3692004 20.5742259,10.128341 18.2399852,10.1203771 C17.6033608,10.1181919 16.9662509,10.2115246 16.329578,10.2643096 C15.3220993,10.3477846 14.3150091,10.4349987 13.3075305,10.518328 C13.0852218,10.5366838 12.9100166,10.4469931 12.8031356,10.2506156 C12.6852314,10.0339399 12.7299068,9.83251217 12.8779668,9.64215615 C13.7523905,8.51764737 14.8620399,7.72859253 16.2232797,7.30315653 C20.0948395,6.09318179 24.2080835,8.6523534 24.8403374,12.6611929 C24.86656,12.8276088 24.8996295,12.9929563 24.929494,13.1587894 L24.929494,14.3242344 Z M1.38659932,7.38012446 C1.52072262,7.09900941 1.74507079,6.99946098 2.05279684,6.99999514 C5.08747002,7.00538533 6.80221054,7.00198611 9.83688372,7.00344292 C10.7104819,7.00387996 11.3634225,7.58111517 11.4579206,8.42713114 C11.5413471,9.17374437 11.0134005,9.92045471 10.2476545,10.0694374 C9.91113228,10.1348966 9.55513738,10.1037695 9.20788332,10.1103737 C8.93487782,10.1155696 8.66172665,10.1113935 8.38197128,10.1113935 C8.37177363,10.1751044 8.36041054,10.2134185 8.36031342,10.2518296 C8.35919654,10.8089123 8.3594879,11.3660921 8.3594879,11.9495431 C8.4377184,11.9495431 8.49890426,11.9539135 8.55931316,11.9489118 C10.0506943,11.8259088 11.5419784,11.7019346 13.0332624,11.5781546 C14.2908261,11.4737987 15.5482928,11.3692486 16.8059051,11.2652812 C17.2251254,11.2306092 17.6438601,11.1859338 18.0637602,11.1657327 C19.4771051,11.0977484 20.6236602,12.108335 20.779927,13.5499905 C20.9192462,14.8357677 19.936776,16.1031406 18.5958344,16.2898546 C18.1311618,16.3545368 17.6441514,16.2864068 17.1696696,16.248627 C15.2829598,16.0983331 13.397124,15.936482 11.5109969,15.77803 C10.6324456,15.704267 9.75408856,15.6277361 8.87553725,15.5541189 C8.71557992,15.5407162 8.55503986,15.5353261 8.37643541,15.5252255 L8.37643541,17.3722617 C8.90481905,17.3722617 9.4208684,17.3619183 9.93628647,17.3747868 C10.5998132,17.3913944 11.0769658,17.7171363 11.3362773,18.3225364 C11.7826913,19.36459 11.0204903,20.4749677 9.86111526,20.4782698 C7.92701067,20.4838542 5.99290609,20.4802608 4.0588015,20.4802608 C2.95823291,20.4802608 3.17749985,20.4735595 2.07702838,20.48366 C1.76230967,20.486525 1.52654985,20.393775 1.38659932,20.1028994 L1.38659932,17.7234491 C1.53995247,17.4447621 1.77512956,17.3643464 2.08926556,17.3659974 C4.16783677,17.3768263 4.92657244,17.3716304 7.00524078,17.3716304 L7.30111813,17.3716304 L7.30111813,15.4333982 C6.9418697,15.3972694 5.04405719,14.6180723 3.79873062,14.2238119 C3.57889855,14.1541766 3.46259685,13.9780487 3.46439357,13.7402979 C3.46614174,13.5034698 3.58482289,13.3390935 3.80295535,13.2584835 C4.06789986,13.1605376 6.93715936,12.0865314 7.30131237,12.0478289 L7.30131237,10.1113935 L6.95692337,10.1113935 C4.88636459,10.111442 4.13568991,10.1063918 2.06522825,10.1169293 C1.75662812,10.1185318 1.53519357,10.0265588 1.38659932,9.75957474 L1.38659932,7.38012446 Z"
                            id="Combined-Shape" transform="rotate(-45.000000) translate(-11.158047, 3) "></path>
                    </g>
                </svg>;
                break;
            case "cart":
                outlet = <svg className="icon" width="24px" height="24px" viewBox="0 0 24 24" version="1.1"
                              xmlns="http://www.w3.org/2000/svg">
                    <g id="2" stroke="none" fill={this.props.color || "#000000"} fillRule="evenodd">
                        <path
                            d="M7.49373434,19 C6.11528822,19 5,20.125 5,21.5 C5,22.875 6.11528822,24 7.49373434,24 C8.87218045,24 10,22.875 10,21.5 C10,20.125 8.87218045,19 7.49373434,19 Z M18.4937343,19 C17.1152882,19 16,20.125 16,21.5 C16,22.875 17.1152882,24 18.4937343,24 C19.8721805,24 21,22.875 21,21.5 C21,20.125 19.8721805,19 18.4937343,19 Z M7.03918651,15.45 L7.0734127,15.314 L8.10019841,13.4666667 L16.5997024,13.4666667 C17.4553571,13.4666667 18.2083333,13.002 18.5962302,12.2993333 L23,4.35466667 L21.014881,3.26666667 L21.0034722,3.26666667 L19.7485119,5.53333333 L16.5997024,11.2 L8.59077381,11.2 L8.44246032,10.894 L5.88690476,5.53333333 L4.8030754,3.26666667 L3.73065476,1 L0,1 L0,3.26666667 L2.28174603,3.26666667 L6.38888889,11.8686667 L4.84871032,14.6453333 C4.66617063,14.9626667 4.56349206,15.3366667 4.56349206,15.7333333 C4.56349206,16.98 5.59027778,18 6.8452381,18 L20.5357143,18 L20.5357143,15.7333333 L7.32440476,15.7333333 C7.17609127,15.7333333 7.03918651,15.6086667 7.03918651,15.45 Z"
                            fillRule="nonzero"></path>
                    </g>
                </svg>;
                break;
            case "cart-add":
                outlet = <svg className="icon" width="24px" height="24px" viewBox="0 0 24 24" version="1.1"
                              xmlns="http://www.w3.org/2000/svg">
                    <g id="2" stroke="none" fill={this.props.color || "#000000"} fillRule="evenodd">
                        <path
                            d="M7.49373434,19 C6.11528822,19 5,20.125 5,21.5 C5,22.875 6.11528822,24 7.49373434,24 C8.87218045,24 10,22.875 10,21.5 C10,20.125 8.87218045,19 7.49373434,19 Z M18.4937343,19 C17.1152882,19 16,20.125 16,21.5 C16,22.875 17.1152882,24 18.4937343,24 C19.8721805,24 21,22.875 21,21.5 C21,20.125 19.8721805,19 18.4937343,19 Z M7.03918651,15.45 L7.0734127,15.314 L8.10019841,13.4666667 L16.5997024,13.4666667 C17.4553571,13.4666667 18.2083333,13.002 18.5962302,12.2993333 L23,4.35466667 L21.014881,3.26666667 L21.0034722,3.26666667 L19.7485119,5.53333333 L16.5997024,11.2 L8.59077381,11.2 L8.44246032,10.894 L5.88690476,5.53333333 L4.8030754,3.26666667 L3.73065476,1 L0,1 L0,3.26666667 L2.28174603,3.26666667 L6.38888889,11.8686667 L4.84871032,14.6453333 C4.66617063,14.9626667 4.56349206,15.3366667 4.56349206,15.7333333 C4.56349206,16.98 5.59027778,18 6.8452381,18 L20.5357143,18 L20.5357143,15.7333333 L7.32440476,15.7333333 C7.17609127,15.7333333 7.03918651,15.6086667 7.03918651,15.45 Z M11.4087302,9.2 L13.6904762,9.2 L13.6904762,5.75 L17.1130952,5.75 L17.1130952,3.45 L13.6904762,3.45 L13.6904762,0 L11.4087302,0 L11.4087302,3.45 L7.98611111,3.45 L7.98611111,5.75 L11.4087302,5.75 L11.4087302,9.2 Z"></path>
                    </g>
                </svg>;
                break;
            default:
                outlet = <i className={"icon fas fa-" + this.props.icon}></i>;
                break;
        }
        return outlet;
    }
}

export default Icon;
