package com.hss.utils.enhance.viewholder.mvvm;

import androidx.annotation.NonNull;

import java.lang.reflect.ParameterizedType;

/**
 * by hss
 * data:2020-04-10
 * desc:
 */
public class TUtil {





    public static <T> T getNewInstance(Object object, int i) {
        if(object!=null){
            try {
                return ((Class<T>) ((ParameterizedType) (object.getClass()
                        .getGenericSuperclass())).getActualTypeArguments()[i])
                        .newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

        }
        return null;

    }

    public static <T> T getInstance(Object object, int i) {
        if (object != null) {
            return (T) ((ParameterizedType) object.getClass()
                    .getGenericSuperclass())
                    .getActualTypeArguments()[i];
        }
        return null;

    }

    public static @NonNull
    <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }


/*    public static <VM extends BaseViewModel> ViewModel createViewModel(ViewModelStoreOwner storeOwner,Class<VM> vmClass){
        VM viewModel = null;
        try {
            viewModel = new ViewModelProvider(storeOwner).get(vmClass);
        }catch (Throwable throwable){
            throwable.printStackTrace();
            viewModel = new ViewModelProvider((storeOwner) ,
                    new SavedStateViewModelFactory(TUtil.app, (SavedStateRegistryOwner) storeOwner))
                    .get(vmClass);
        }
        return viewModel;

    }*/
}

