package neo.tao.service.impl;

import neo.tao.mvc.annotation.Service;
import neo.tao.service.DemoService;

/**
 * @Author neotao
 * @Date 2018/8/3
 * @Version V0.0.1
 * @Desc
 */
@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String getByName(String name) {
        return "aaa";
    }
}
