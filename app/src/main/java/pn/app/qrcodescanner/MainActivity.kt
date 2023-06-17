package pn.app.qrcodescanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import pn.app.qrcodescanner.bottomnavbar.fragments.QrcodeScanFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navbar = findViewById<BottomNavigationView>(R.id.navbar)
        initNavbar()
        navbar.setOnItemSelectedListener {
            if (it.itemId==R.id.qr) {
                Log.i("choice","1")
                setfragment(1)
            }
            true
        }

    }

    private fun setfragment(choice : Int) {
        val framelayout = findViewById<FrameLayout>(R.id.fragmentview)
        val scanfragment = QrcodeScanFragment()
        supportFragmentManager.beginTransaction().apply {
            if (choice==1) {
                replace(R.id.fragmentview,scanfragment)
                commit()
            }
        }
    }

    private fun initNavbar() {
        val navbar = findViewById<BottomNavigationView>(R.id.navbar)
        navbar.selectedItemId = R.id.qr
        setfragment(1)

    }
}