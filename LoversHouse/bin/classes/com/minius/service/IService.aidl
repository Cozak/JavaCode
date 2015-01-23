package com.minius.service;
import com.minius.service.ICallback;

  
     interface IService {  
             void registerCallback(ICallback cb);  
             void unregisterCallback(ICallback cb);  
     }