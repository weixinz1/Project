function output(output_port,number_of_retries,message)
%this is the function of output method in server
%this function will output the "message" from the "output_port" port
%number_of_retries means how many times does this function retry;
import java.net.ServerSocket
import java.io.*
retry= 0;
server_socket  = [];
output_socket  = [];

while true
    retry=retry+1;
    try
        if ((number_of_retries > 0) && (retry > number_of_retries))
                fprintf(1, 'Too many retries\n');
                break;
        end
        fprintf(1, ['Try %d waiting for client to connect to this ' ...
                        'host on port : %d\n'], retry, output_port);
        server_socket = ServerSocket(output_port);
        server_socket.setSoTimeout(10000);
        output_socket = server_socket.accept;
        fprintf(1, 'Client connected\n');
        
        output_stream   = output_socket.getOutputStream;
        d_output_stream = DataOutputStream(output_stream);
        fprintf(1, 'Writing %d bytes\n', length(message))
        d_output_stream.writeBytes(char(message));
        d_output_stream.flush;
        
        server_socket.close;
        output_socket.close;
        break;
        
    catch
        if ~isempty(server_socket)
            server_socket.close
        end

        if ~isempty(output_socket)
            output_socket.close
        end
        pause(1);
    end
end


end

