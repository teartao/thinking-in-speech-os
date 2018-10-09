package neo.tao.action;

import neo.tao.mvc.annotation.Autowired;
import neo.tao.mvc.annotation.Controller;
import neo.tao.mvc.annotation.RequestMapping;
import neo.tao.mvc.annotation.RequestParam;
import neo.tao.service.DemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author neotao
 * @Date 2018/8/3
 * @Version V0.0.1
 * @Desc
 */
@Controller
@RequestMapping("/act1")
public class FirstAction {
    @Autowired
    private DemoService demoService;

    @RequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam("sName") String name) {
        String result = demoService.getByName(name);
        try {
            response.getWriter().write(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
