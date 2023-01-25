package com.example.fotocamera;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//подключиться к обьекту камера 
//рамка камеры
//включить камеру 
//сделать фото при нажатии
//сохранить фото 
//закрыть камеру

public class MainScreen extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback, Camera.AutoFocusCallback
{
//переменные 
//камера 
    private Camera camera;
    //Camera берет holder и с его помощью выводит изображение на surface.Именно с этим объектом умеет работать Camera. 
    private SurfaceHolder surfaceHolder;
  //Camera используется, чтобы получить изображение с камеры. А чтобы это изображение в приложении отобразить, будем использовать SurfaceView.
    private SurfaceView preview;
    //кнопка
    private Button shotBtn;
   //активность как форма 
    @Override
    // метод создать рамку втавить изображение и кнопку 
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // если хотим, чтобы приложение постоянно имело портретную ориентацию
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // если хотим, чтобы приложение было полноэкранным
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // и без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        // наше SurfaceView имеет имя SurfaceView01
        //обязательным условием при работе с камерой является создание окна предпросмотра
        preview = (SurfaceView) findViewById(R.id.SurfaceView01);

        surfaceHolder = preview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // кнопка имеет имя Button01
        shotBtn = (Button) findViewById(R.id.Button01);
        shotBtn.setText("Shot");
        shotBtn.setOnClickListener(this);
    }
    // Каждое приложение запускается в виде отдельного процесса, что позволяет системе давать одним процессам более высокой приоритет,
    //Благодаря этому, например, при работе с одними приложениями Android позволяет не блокировать входящие звонки. После прекращения    
    //работы с приложением, система освобождает все связанные ресурсы и переводит приложение в разряд низкоприоритетного и закрывает его.
   //отображается на экране устройства, и пользователь может с ней взаимодействовать
    @Override
    protected void onResume()
    {
        super.onResume();
        //включить камеру
        camera = Camera.open();
    }
//Если пользователь решит перейти к другой activity, то система вызывает метод onPause, а activity переходит в состояние Paused. 
    @Override
    protected void onPause()
    {
        super.onPause();

        if (camera != null)
        {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
//интерфейс предлагает реализовать три метода
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Size previewSize = camera.getParameters().getPreviewSize();
        float aspect = (float) previewSize.width / previewSize.height;

        int previewSurfaceWidth = preview.getWidth();
        int previewSurfaceHeight = preview.getHeight();

        LayoutParams lp = preview.getLayoutParams();

        // здесь корректируем размер отображаемого preview, чтобы не было искажений

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
        {
            // портретный вид
            camera.setDisplayOrientation(90);
            lp.height = previewSurfaceHeight;
            lp.width = (int) (previewSurfaceHeight / aspect);
            ;
        }
        else
        {
            // ландшафтный
            camera.setDisplayOrientation(0);
            lp.width = previewSurfaceWidth;
            lp.height = (int) (previewSurfaceWidth / aspect);
        }

        preview.setLayoutParams(lp);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    @Override
    public void onClick(View v)
    {
        if (v == shotBtn)
        {
            // либо делаем снимок непосредственно здесь
            // 	либо включаем обработчик автофокуса

            //camera.takePicture(null, null, null, this);
            camera.autoFocus(this);
        }
    }

    @Override
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // сохраняем полученные jpg в папке /sdcard/CameraExample/
        // имя файла - System.currentTimeMillis()

        try
        {
            File saveDir = new File("/sdcard/CameraExample/");

            if (!saveDir.exists())
            {
                saveDir.mkdirs();
            }

            FileOutputStream os = new FileOutputStream(String.format("/sdcard/CameraExample/%d.jpg", System.currentTimeMillis()));
            os.write(paramArrayOfByte);
            os.close();
        }
        catch (Exception e)
        {
        }

        // после того, как снимок сделан, показ превью отключается. необходимо включить его
        paramCamera.startPreview();
    }

    @Override
    public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
    {
        if (paramBoolean)
        {
            // если удалось сфокусироваться, делаем снимок
            paramCamera.takePicture(null, null, null, this);
        }
    }

    @Override
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // здесь можно обрабатывать изображение, показываемое в preview
    }
}
