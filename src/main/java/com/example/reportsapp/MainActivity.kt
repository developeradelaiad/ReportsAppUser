package com.example.reportsapp

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.reportsapp.bottom_fragment.HistoryFragment
import com.example.reportsapp.bottom_fragment.ReceivieOrderFragment
import com.example.reportsapp.bottom_fragment.SendOrderFragment
import com.example.reportsapp.callback.ReportsCallable
import com.example.reportsapp.data.DataRecycler
import com.example.reportsapp.data.DataZero
import com.example.reportsapp.databinding.ActivityMainBinding
import com.example.reportsapp.nav_fragment.ProfileFragment
import com.example.reportsapp.nav_fragment.SettingsFragment
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private val REQUEST_PERMISSION_CODE = 1001
    private lateinit var orders: ArrayList<DataZero> // متغير لتخزين الطلبات
private lateinit var shared: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         shared = getSharedPreferences("Save_Client",MODE_PRIVATE)
        val id = shared.getInt("id",-1)
          orders = ArrayList<DataZero>()
        sendNotification(id)
//        binding.mainPage1.adapter = FragmentAdapter(this)
//        val tabName = arrayOf("Send Order", "Receive Order", "All Order")
//        TabLayoutMediator(binding.mainTab, binding.mainPage1) { tab, position ->
//            tab.text = tabName[position]
//        }.attach()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)

//        val navigationView = findViewById<NavigationView>(R.id.bottom_navigation)
//        val header = navigationView.getHeaderView(0)
//        val userNameTxt = header.findViewById<TextView>(R.id.userNameTxt)
//        val emailTxt = header.findViewById<TextView>(R.id.emailTxt)


        binding.bottomNavigation.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        drawerLayout.setScrimColor(getColor(R.color.blue))


        /// Default Navigation bar Tab Selected
        replaceFragment(HistoryFragment())
        binding.bottomNavigation.setCheckedItem(R.id.nav_home)

        //سيشن تسجيل دخول
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        // تحقق من حالة تسجيل الدخول
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            // الانتقال إلى صفحة تسجيل الدخول
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_receive -> {
                    replaceFragment(ReceivieOrderFragment())
                    setTitle(R.string.status)
                }

                R.id.bottom_history -> {
                    replaceFragment(HistoryFragment())
                    setTitle(R.string.history_rep)
                }
            }
            true
        }
        binding.addFab.setOnClickListener {
            replaceFragment(SendOrderFragment())
        }
        toolbar.setSubtitleTextColor(getColor(R.color.blue))
        toolbar.setTitleTextColor(getColor(R.color.blue))
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedMethod()
        }

    }
    private fun onBackPressedMethod() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(SendOrderFragment())
                title = getString(R.string.send)
            }

            R.id.nav_profile -> {
                replaceFragment(ProfileFragment())
                setTitle(R.string.profile)
            }

            R.id.nav_setting -> {
                replaceFragment(SettingsFragment())
                setTitle(R.string.settings)
            }

            R.id.nav_share -> {
                shareApp()
                setTitle(R.string.share_rep)
            }

            R.id.nav_logout -> {
                setTitle(R.string.log_out)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT, "جرب تطبيقنا الجديد! يمكنك تحميله قريبًا. \n" +
                        "للمزيد من المعلومات، تواصل معنا على: [يجب وضع البريد الخاص بالشركه]"
            )
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق عبر"))
    }
   private fun sendNotification(id:Int){
           val retrofit= Retrofit.Builder()
               .baseUrl("http://youre_link/")
               .addConverterFactory(GsonConverterFactory.create())
               .build()
           val c = retrofit.create(ReportsCallable::class.java)
           c.getMainThree(id).enqueue(object : Callback<DataRecycler>{
   override fun onResponse(p0: Call<DataRecycler?>, p1: Response<DataRecycler?>) {
           if (p1.isSuccessful) {
               val body = p1.body()!!.zero
               if (body != null && body != null) {
                   sendNewOrdersNotification(body)
                   Log.e("NotificationSuccess", body.toString())
               }
           }
    }

    override fun onFailure(p0: Call<DataRecycler?>, p1: Throwable) {
        Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        Log.e("NotificationFailure",p1.message.toString())
    }
})
    }
    private fun sendNewOrdersNotification(orders: ArrayList<DataZero>) {
        // تأكد من فحص الإذن قبل المتابعة
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val channel = NotificationChannel(
                "new_orders_channel",
                "New Orders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new orders"
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // التحقق من أن النشاط لا يزال موجودًا
        if (isFinishing || isDestroyed) {
            return // لا ترسل الإشعارات إذا كان النشاط غير صالح
        }

        // معالجة جميع الأوامر في القائمة
        if (orders != null) {
            for (order in orders) {
                val orderDetails =
                    "Report Name: ${order.report_name}, Name: ${order.username}, Email: ${order.email},Attach File:${order.report_attach_file}"
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val intent = Intent(this, MainActivity::class.java) // إنشاء الـ Intent
                val pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                val notificationBuilder = NotificationCompat.Builder(this, "new_orders_channel")
                    .setSmallIcon(R.drawable.ic_order) // تأكد من أن لديك أيقونة
                    .setContentTitle("New Order Available")
                    .setContentText(orderDetails)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .setSound(Uri.parse("android.resource://" + packageName + "/" + R.raw.level))
                    .addAction(
                        R.drawable.ic_history_24,
                        "Go to History",
                        pendingIntent
                    ) // إضافة الـ PendingIntent

                // إرسال الإشعار
                with(NotificationManagerCompat.from(this)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(POST_NOTIFICATIONS),
                                REQUEST_PERMISSION_CODE
                            )
                            return
                        }
                    }
                    Log.d(
                        "NotificationORDER",
                        "Sending notification for Order ID: ${order.report_id}"
                    )
                    notificationManager.notify(
                        order.report_id.hashCode(),
                        notificationBuilder.build()
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // إذا تم منح الإذن، أرسل الإشعارات
                Log.d("Permission", "Notification permission granted.")
                sendNewOrdersNotification(orders)
            } else {
                // الإذن مرفوض، يمكن للمستخدم تعطيل الإشعارات
                Toast.makeText(this, "Permission denied. Can't send notifications.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}