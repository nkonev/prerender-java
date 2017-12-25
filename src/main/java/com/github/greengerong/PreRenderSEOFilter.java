package com.github.greengerong;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PreRenderSEOFilter implements Filter {
    public static final List<String> PARAMETER_NAMES = Lists.newArrayList(
            PreRenderConstants.InitFilterParams.PRE_RENDER_EVENT_HANDLER, PreRenderConstants.InitFilterParams.PROXY,
            PreRenderConstants.InitFilterParams.PROXY_PORT, PreRenderConstants.InitFilterParams.PRERENDER_TOKEN,
            PreRenderConstants.InitFilterParams.FORWARDED_URL_HEADER, PreRenderConstants.InitFilterParams.FORWARDED_URL_PREFIX_HEADER,
            PreRenderConstants.InitFilterParams.FORWARDED_URL_PREFIX, PreRenderConstants.InitFilterParams.CRAWLER_USER_AGENTS,
            PreRenderConstants.InitFilterParams.EXTENSIONS_TO_IGNORE, PreRenderConstants.InitFilterParams.WHITELIST,
            PreRenderConstants.InitFilterParams.BLACKLIST, PreRenderConstants.InitFilterParams.PRERENDER_SERVICE_URL
    );
    private PrerenderSeoService prerenderSeoService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.prerenderSeoService = new PrerenderSeoService(toMap(filterConfig));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        boolean isPrerendered = prerenderSeoService.prerenderIfEligible(
                (HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        if (!isPrerendered) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        prerenderSeoService.destroy();
    }

    protected void setPrerenderSeoService(PrerenderSeoService prerenderSeoService) {
        this.prerenderSeoService = prerenderSeoService;
    }

    protected Map<String, String> toMap(FilterConfig filterConfig) {
        Map<String, String> config = Maps.newHashMap();
        for (String parameterName : PARAMETER_NAMES) {
            config.put(parameterName, filterConfig.getInitParameter(parameterName));
        }
        return config;
    }
}

