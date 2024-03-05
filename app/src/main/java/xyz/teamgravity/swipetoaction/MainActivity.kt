package xyz.teamgravity.swipetoaction

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xyz.teamgravity.swipetoaction.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SwipeTouchAdapter.SwipeTouchListener {

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
            val swipe = SwipeTouchAdapter(
                leftAction = SwipeTouchAdapter.SwipeTouchAction.fromResource(
                    context = this@MainActivity,
                    icon = R.drawable.ic_delete,
                    background = R.color.red
                ),
                rightAction = SwipeTouchAdapter.SwipeTouchAction.fromResource(
                    context = this@MainActivity,
                    icon = R.drawable.ic_edit,
                    background = R.color.blue
                )
            )
            swipe.listener = this@MainActivity
            swipe.attachToRecyclerView(recyclerview)
        }
    }

    private fun observeNames() {
        lifecycleScope.launch {
            viewmodel.names.collectLatest { data ->
                adapter.submitList(data)
            }
        }
    }

    override fun onSwipeClickLeft(position: Int) {
        viewmodel.onDelete(position)
    }

    override fun onSwipeClickRight(position: Int) {
        adapter.notifyItemChanged(position)
        Toast.makeText(this, "Edit $position", Toast.LENGTH_SHORT).show()
        // TODO handle action
    }
}