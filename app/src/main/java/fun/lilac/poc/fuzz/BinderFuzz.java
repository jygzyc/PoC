package fun.lilac.poc.fuzz;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.elvishew.xlog.XLog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fun.lilac.poc.utils.XLogUtil;

public class BinderFuzz {
    private static final String TAG = "BinderFuzz";

    BinderFuzz(){
        XLogUtil.initXLog();
    }

    //获取所有运行的services
    public String[] getServices() {
        String[] services = null;
        try {
            services = (String[]) Class.forName("android.os.ServiceManager")
                    .getDeclaredMethod("listServices").invoke(null);
        } catch (ClassCastException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            XLog.e(TAG, e);
        }
        return services;
    }

    //获取所有服务的IBinder对象
    public static IBinder getIBinder(String service) {
        IBinder serviceBinder = null;
        try {
            serviceBinder = (IBinder) Class.forName("android.os.ServiceManager")
                    .getDeclaredMethod("getService", String.class).invoke(null, service);
        } catch (ClassCastException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            XLog.e(TAG, e);
        }
        return serviceBinder;
    }

    //利用反射获取对应接口的所有code
    public HashMap<String,Integer> getBinderCode(String interfaceDescriptor) {
        HashMap<String, Integer> codes = new HashMap<>();

        if (interfaceDescriptor == null)
            return codes;

        try {
            Class<?> cStub = Class
                    .forName(interfaceDescriptor + "$Stub");
            Field[] f = cStub.getDeclaredFields();
            for (Field field : f) {
                field.setAccessible(true);
                String k= field.toString().split("\\$Stub\\.")[1];
                if (k.contains("TRANSACTION"))
                    codes.put(k, (int)field.get(this));
            }
        } catch (Exception e) {
            XLog.e(TAG, e);
        }
        return codes;
    }

    //利用反射获取对应接口所有调用的参数类型
    public HashMap<String, List<String>>
    getBinderCallParameter(String interfaceDescriptor,
                           HashMap<String, Integer> codes) {
        HashMap<String, List<String>> ret = new HashMap();

        if (interfaceDescriptor == null)
            return ret;

        try {
            Class<?> cStub = Class
                    .forName(interfaceDescriptor + "$Stub$Proxy");
            Method[] m = cStub.getDeclaredMethods();

            for (Method method : m) {
                int func_code = 0;
                List<String> func_parameter = new ArrayList<>();

                method.setAccessible(true);
                String func_name = method.toString().split("\\$Stub\\$Proxy\\.")[1];
                func_parameter.add(func_name);

                for (String key : codes.keySet()) {
                    if (func_name.contains(key.substring("TRANSACTION_".length())))
                        func_code = codes.get(key);
                }

                if (func_code == 0)
                    continue;

                Class<?>[] ParameterTypes = method.getParameterTypes();
                for (int k=0; k < ParameterTypes.length; k++) {
                    func_parameter.add(ParameterTypes[k].toString());
                }

                ret.put(Integer.toString(func_code), func_parameter);
            }
        } catch (Exception e) {
            XLog.e(TAG, e);
        }
        return ret;
    }


    //构造transact发送的数据
    public Parcel createBindData(String service,
                                 String interfaceDescriptor,
                                 List<String> func_parameter){
        Parcel bindData = Parcel.obtain();
        bindData.writeInterfaceToken(interfaceDescriptor);
        for(String param : func_parameter){
            if(param == "java.lang.String"){
                bindData.writeString("Android");
            }else if(param == "Int"){
                bindData.writeInt(1);
            }else if(param == "Float"){

            }
        }

        return bindData;
    }




    //Binder调用
    public void fuzz (int code, String service) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        IBinder serviceBinder = BinderFuzz.getIBinder(service);
        serviceBinder.transact(code, data, reply, 0);
    }
}
