package com.example.subwayseat

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_choose_seat1.*
import kotlinx.android.synthetic.main.activity_choose_seat2.*
import kotlinx.android.synthetic.main.activity_choose_train_up.*


class chooseSeat2 : AppCompatActivity() {
    val TRAIN = 1000
    val BLOCK = "4-1"
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_seat2)

        var location_info2: ArrayList<Int>  = ArrayList()

        if (intent.hasExtra("intentKey")) {
            val location_info:ArrayList<Int> = intent.getSerializableExtra("intentKey") as ArrayList<Int>
            val loc1 = location_info.get(1)
            val loc2 = location_info.get(2)
            tv_location4.text = loc1.toString()+ "-2"
            tv_location5.text = loc1.toString()+ "-3"
            tv_location6.text = loc1.toString()+ "-2"
            tv_location7.text = loc1.toString()+ "-3"
            //다음 인텐트로 보낼 location info
            location_info2.add(0, CURRENT_TRAIN_NO)
            location_info2.add(1, loc1)
            location_info2.add(2, loc2)

        }

        btn_seat21.setOnClickListener{seatPopup(21, btn_seat21)}
        btn_seat22.setOnClickListener{seatPopup(22, btn_seat22)}
        btn_seat23.setOnClickListener{seatPopup(23, btn_seat23)}
        btn_seat24.setOnClickListener{seatPopup(24, btn_seat24)}
        btn_seat25.setOnClickListener{seatPopup(25, btn_seat25)}
        btn_seat26.setOnClickListener{seatPopup(26, btn_seat26)}
        btn_seat27.setOnClickListener{seatPopup(27, btn_seat27)}
        btn_seat28.setOnClickListener{seatPopup(28, btn_seat28)}
        btn_seat29.setOnClickListener{seatPopup(29, btn_seat29)}
        btn_seat30.setOnClickListener{seatPopup(30, btn_seat30)}
        btn_seat31.setOnClickListener{seatPopup(31, btn_seat31)}
        btn_seat32.setOnClickListener{seatPopup(32, btn_seat32)}
        btn_seat33.setOnClickListener{seatPopup(33, btn_seat33)}
        btn_seat34.setOnClickListener{seatPopup(34, btn_seat34)}


        //이전 버튼
        btn_prev2.setOnClickListener{
            val nextIntent = Intent(this, chooseSeat1::class.java)
            nextIntent.putExtra("intentKey", location_info2)
            startActivity(nextIntent)
        }
        //다음 버튼
        btn_next2.setOnClickListener{
            val nextIntent = Intent(this, chooseSeat3::class.java)
            nextIntent.putExtra("intentKey", location_info2)
            startActivity(nextIntent)
        }

    }

    //뒤로 가기 버튼 -> 열차 선택하는 화면으로 (chooseTrainUp activity)
    override fun onBackPressed() {
        startActivity(Intent(this, chooseTrainUp::class.java))
        finish()
    }


    // 1. 좌석 버튼 클릭시 나타나는 팝업창
    private fun seatPopup(seat_num: Int, button: Button){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.train_info_popup_layout, null)
        val tv_popup_info = view.findViewById<TextView>(R.id.tv_popup_info)
        tv_popup_info.text = seat_num.toString() +"번 좌석을 선택하시겠습니까?"

        //팝업창
        val alertDialog = AlertDialog.Builder(this)
            .setPositiveButton("확인"){dialog, which ->
//                button.setBackgroundResource(R.drawable.seat_red)
                println("좌석 number : "+seat_num)
                checkPopup(seat_num.toString(), input_dst)
            }
            .setNeutralButton("취소", null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
    }


    // 2. seatPopup 팝업창 -> 확인 버튼 클릭 시 도착역 등 정보 확인 용 새로운 팝업창 나타남
    private fun checkPopup(seat_num:String, dst:String){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.check_popup_layout, null)
        val et_dst = view.findViewById<TextView>(R.id.et_dst)
        val spn_station:Spinner = view.findViewById<Spinner>(R.id.spn_station2)
        val tv_left_info = view.findViewById<TextView>(R.id.tv_left_info)
        var cb_alarm = view.findViewById<CheckBox>(R.id.cb_alarm)
        val tv_seat1 = view.findViewById<TextView>(R.id.tv_seat1)
        val tv_dst_info = view.findViewById<TextView>(R.id.tv_dst_info)
        var final_dst:String = dst
        var exist = 0

        tv_seat1.setText(seat_num)


        //입력받은 하차역으로 팝업창의 하차역 기본 설정
        et_dst.setText("   "+dst)


        //도착역 선택할 수 있는 스피너
//        val station_list = STATION_LOCATION_X.keys.toTypedArray()
//        station_list.sort()
//
//        spn_station.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, station_list)
//
//        //아이템 선택 리스너
//        spn_station.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                println("nothing selected!")
//            }
//
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
//                et_dst.setText("   "+station_list[position].toString())
//                final_dst = station_list[position]

        var stt_now = -1
        val stt_dst = STATION_INDEX[final_dst]


        // 도착역까지 남은 시간 계산
        // 1) 현재 열차의 위치 받아오기
        val db_station_info = myRef.child("SubwayLocation").child(line_no).child(line_updown)
        db_station_info.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println("database error!")
            }

            override fun onDataChange(p0: DataSnapshot) {
//                        println("p0 " + p0.toString())
                for (snapshot in p0.children) {
//                            println(final_dst + "  " + snapshot.key)
//                            println(STATION_INDEX[snapshot.child("현재역").value.toString()])
//                            println(STATION_INDEX[snapshot.child("현재역").toString().toInt()])
                    if (snapshot.key == CURRENT_TRAIN_NO.toString()) {
                        stt_now = STATION_INDEX[snapshot.child("현재역").value.toString()]!!
                        println("현재역 : "+snapshot.child("현재역").value.toString())
                        if (snapshot.child("현재역").value.toString().length > 8){
                            tv_left_info.textSize = 9F //fontsize 변경 -- 보완 필요
                        }
                    }
                }
                //2) cost_time 계산
                println("calculate time index : "+stt_dst.toString()+" "+stt_now.toString())
                val cost_time = stt_dst?.let { it1 -> calculateTime(stt_now, it1) }


                tv_left_info.text = final_dst + " 도착까지 " + cost_time + "분 남았습니다."
            }
        })







        //팝업창
        val alertDialog = AlertDialog.Builder(this)
            .setPositiveButton("확인") { dialog, which ->

                // 1) 1개 역 전에 알림 여부 체크박스
                cb_alarm.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        //알림 function
                        dstPushAlarm()
                    }
                }
                // To2) firebase에서 좌석 정보 업데이트 - user 정보, 좌석 정보, 도착역
                updateSeatInfo(TRAIN, BLOCK, seat_num, "sange1104", 1, final_dst)

            }

            .setNeutralButton("취소", null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()

    }



    // 3. checkPopup 팝업창 -> 확인 버튼 클릭 시 firebase에 좌석 정보 업데이트
    private fun updateSeatInfo(train:Int, block:String, seat:String, userid:String, status:Int, dst:String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference()
        val seatRef = myRef.child("SeatStatus").child("4호선").child(train.toString()).child(block)
        val updateInfo = seatInfo(userid, status, dst)
        seatRef.child(seat).setValue(updateInfo) //시간 계산 어디에서??
    }



    // 4. 푸시 알림 function
    private fun dstPushAlarm() {
        // 해당 열차가 입력받은 도착역 1정거장 전에 도착했는지 확인
        //firebase ondatachange
        // 푸시 알림
        var title = "subwaySeat"
        var content = "하차 1정거장 전입니다"
//        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.phone)

        val NOTIFICAION_ID = 1001
        var builder = getNotificationBuilder("style","style Notification")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
//            .setLargeIcon(bitmap)
            .setShowWhen(true)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        var notification = builder.build()

        var manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(10 ,notification)
//        NotificationManagerCompat.from(this).notify(NOTIFICAION_ID, builder.build())

    }
    //푸시 알림 채널 설정
    fun getNotificationBuilder(id:String, name:String): NotificationCompat.Builder {
        var builder: NotificationCompat.Builder? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            manager.createNotificationChannel(channel)

            builder = NotificationCompat.Builder(this, id)
        } else {
            builder = NotificationCompat.Builder(this)
        }
        return builder
    }


}