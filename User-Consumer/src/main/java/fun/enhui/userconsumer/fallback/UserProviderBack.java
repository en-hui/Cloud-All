package fun.enhui.userconsumer.fallback;

import fun.enhui.userconsumer.api.ConsumerApi;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * @author 胡恩会
 * @date 2020/11/26 23:22
 */
@Component
public class UserProviderBack implements ConsumerApi {
    @Override
    public Map<Integer, String> getMap(Integer id) {
        return null;
    }

    @Override
    public Map<Integer, String> getMap2(Integer id, String name) {
        return null;
    }

    @Override
    public Map<String, String> getMap3(Map<String, Object> map) {
        return null;
    }

    @Override
    public Map<String, String> postMap(Map<String, Object> map) {
        return null;
    }

    @Override
    public String alive() {
        return "降级了";
    }
}
