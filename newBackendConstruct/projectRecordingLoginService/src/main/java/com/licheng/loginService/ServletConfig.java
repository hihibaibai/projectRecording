package com.licheng.loginService;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目里面所有的request和response header都在这里配置，包括用户鉴权
 */
@WebFilter
@Component
public class ServletConfig implements Filter {
//    @Resource
//    private UserService userService;

    private List<String> ignoreList = new ArrayList<>();

    @PostConstruct
    public void postConstruct(){
        // 在此处添加例外
        this.ignoreList.add("/user/login");
        this.ignoreList.add("/user/register");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse rep = (HttpServletResponse) response;

        rep.setHeader("Access-control-Allow-Origin", req.getHeader("Origin"));
        rep.setHeader("Access-control-Allow-Methods", "GET,POST");

        String url = req.getRequestURI();
        if (req.getMethod().equals("OPTIONS")) { // 处理预请求
            rep.setStatus(HttpServletResponse.SC_NO_CONTENT);
            rep.setHeader("Access-control-Allow-headers", req.getHeader("Access-Control-Request-Headers"));
            return;
        }
//        if (req.getHeader("login-token") == null) {
//            if (!urlIsInExceptionList(url)){
//                rep.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return;
//            }
//        }
//        else {
//            // 这里放判断密码是否正确的方法
//            userService.isUserAuthorized(req.getHeader("Login-token"));
//        }
        chain.doFilter(req, rep);

    }

    private Boolean urlIsInExceptionList(String url) {
        Boolean result = false;
        for (String ignoreString : this.ignoreList) {
            if (url.contains(ignoreString)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
