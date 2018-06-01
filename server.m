while true
        pic=webread('https://tinadbx.000webhostapp.com/pictures/.JPG'); %read picture from the web server;
        pic=imresize(pic,[224,224]);
        [a,b]=classify(net,pic);
        [b,index]=sort(b,'descend');
        str1=convnet.Layers(end).ClassNames(index(1))+" with probability ";
        str2=convnet.Layers(end).ClassNames(index(2))+" with probability ";
        message=sprintf('%s%2.2f%%\n%s%2.2f%%',str1,b(1)*100,str2,b(2)*100);
        output(3002,1,message) %use output function to return the result;
end