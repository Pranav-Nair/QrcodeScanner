package pn.app.qrcodescanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import pn.app.qrcodescanner.bottomnavbar.fragments.QrCodeGenerator
import pn.app.qrcodescanner.bottomnavbar.fragments.QrcodeScanFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navbar = findViewById<BottomNavigationView>(R.id.navbar)
        initNavbar()
        navbar.setOnItemSelectedListener {
            if (it.itemId==R.id.qr) {
                setfragment(1)
            } else if (it.itemId==R.id.qrgen) {
                setfragment(2)
            }
            true
        }

    }

    private fun setfragment(choice : Int) {
        val framelayout = findViewById<FrameLayout>(R.id.fragmentview)
        val scanfragment = QrcodeScanFragment()
        val genfragment = QrCodeGenerator()
        supportFragmentManager.beginTransaction().apply {
            if (choice==1) {
                replace(R.id.fragmentview,scanfragment)
                commit()
            } else if (choice==2) {
                replace(R.id.fragmentview,genfragment)
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