package com.jhealth.diabetesapp.presentation.article

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FragmentArticleBinding

class ArticleFragment : Fragment() {
    private var _binding:FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<ArticleFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = args.args
        setUpPopUpMenu()
        binding.apply {
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }

            swipeRefresh.setOnRefreshListener {
                binding.webView.reload()
                binding.swipeRefresh.isRefreshing = false
            }

            val chromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.progressBar.progress = newProgress
                }

            }
            val webClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.isVisible = false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.progressBar.isVisible = true
                }

            }

            webView.apply {
                webViewClient = webClient
                loadUrl(url)
                webChromeClient = chromeClient
                settings.javaScriptEnabled = true
                canGoBack()

                setOnKeyListener { view, i, keyEvent ->
                    val webView = binding.webView
                    if (i == KeyEvent.KEYCODE_BACK && keyEvent.action == MotionEvent.ACTION_UP && webView.canGoBack()) {
                        webView.goBack()
                        return@setOnKeyListener true
                    } else return@setOnKeyListener false
                }
            }
        }

    }

    private fun setUpPopUpMenu(){
        binding.menuBtn.setOnClickListener {


            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.article_frag_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {item->
                when (item.itemId) {
                    R.id.refreshPage -> {
                        binding.webView.reload()
                        true
                    }
                    R.id.openOutside -> {
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(args.args)
                            startActivity(this)
                        }
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }
            popupMenu.show()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }


}