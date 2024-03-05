package xyz.teamgravity.swipetoaction

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.teamgravity.swipetoaction.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewmodel by viewModels<MainViewModel>()

    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()
        observe()
    }

    private fun updateUI() {
        systemBars()
        recyclerview()
    }

    private fun observe() {
        observeNames()
    }

    private fun systemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }
    }

    private fun recyclerview() {
        binding.apply {
            adapter = MainAdapter()
            recyclerview.setHasFixedSize(true)
            recyclerview.adapter = adapter
        }
    }

    private fun observeNames() {
        lifecycleScope.launch {
            viewmodel.names.collectLatest { data ->
                adapter.submitList(data)
            }
        }
    }
}