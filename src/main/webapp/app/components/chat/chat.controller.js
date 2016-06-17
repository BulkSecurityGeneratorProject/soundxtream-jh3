(function() {
	'use strict';
	angular
	.module('soundxtream3App')
	.controller('ChatCtrl', ChatCtrl);

	ChatCtrl.$inject = ['$q', '$scope', 'Pubnub' , '$rootScope', 'Principal', '$stateParams', '$anchorScroll'];

	function ChatCtrl ($q, $scope, Pubnub, $rootScope, Principal, $stateParams, $anchorScroll) {

		Principal.identity().then(function(account){
			vm.user = account;
		});

		var vm = this;

		vm.messages = [];
    	vm.channel = $stateParams.name;
    	vm.firstMessageTimeToken = null;
  		vm.messagesAllFetched = false;

    	Pubnub.time(function(time){
        	vm.firstMessageTimeToken = time;
      	})

  		var populate = function(){

		    var defaultMessagesNumber = 10;

		    Pubnub.history({
		     channel: vm.channel,
		     callback: function(m){
		        // Update the timetoken of the first message
		        angular.extend(vm.messages, m[0]);  

		        console.log(m);
		        
		        if(m[0].length < defaultMessagesNumber){
		          vm.messagesAllFetched = true;
		        }

		        $rootScope.$digest()
		        $rootScope.$emit('factory:message:populated')
		        
		     },
		     count: defaultMessagesNumber, 
		     reverse: false
		    });

		};

			var getMessages = function() {
		    if (_.isEmpty(vm.messages))
		      populate();
		    return vm.messages;
  		};
  		getMessages();



		  var fetchPreviousMessages = function(){
		  	var messages2 = getMessages();
		  	console.log(messages2);
		    var defaultMessagesNumber = 1;

		    var deferred = $q.defer()

		    Pubnub.history({
		     channel: vm.channel,
		     callback: function(m){
		        // Update the timetoken of the first message
		        vm.timeTokenFirstMessage = m[1]
		        Array.prototype.unshift.apply(vm.messages,m[0])
		        
		        if(m[0].length < defaultMessagesNumber){
		          vm.messagesAllFetched = true;
		        }

		        $rootScope.$digest()
		        deferred.resolve(m)

		     },
		     error: function(m){
		        deferred.reject(m)
		     },
		     count: defaultMessagesNumber, 
		     start: vm.timeTokenFirstMessage,
		     reverse: false
		    });
		    return deferred.promise
		  };


		var messagesAllFetched = function() {
		    return vm.messagesAllFetched;
		};

    	vm.avatarUrl = function() {
        	return '//robohash.org/' + $rootScope.uuid + '?set=set2&bgset=bg2&size=70x70';
    	};

    	vm.sendMessage = function() {
	       // Don't send an empty message 
	       if (!vm.messageContent ||
	            vm.messageContent === '') {
	            return;
	        }
	        Pubnub.publish({
	            channel: vm.channel,
	            message: {
	                content: vm.messageContent,
	                sender_uuid: vm.user.login,
	                date: new Date()
	            },
	            callback: function(m) {
	                console.log(m);
	            }
	        });
	        // Reset the messageContent input
	        vm.messageContent = '';

	    }

	    Pubnub.subscribe({
	        channel: vm.channel,
	        triggerEvents: ['callback']
	    });

	    

	    $scope.scrollDown = function(time) {
	        var $elem = $('.collection');

	        $elem.animate({
	            scrollTop: $('.collection')[0].scrollHeight
	        },{
	        	complete: function () {
	        		$elem.scrollTop($('.collection')[0].scrollHeight);
	        	}
	        }, time);

	    };

	   	$scope.scrollDown(2000);

	    $('.collection').on('scroll', function() {
	    	console.log($(this).scrollTop() + " , " + $(this)[0].scrollHeight);
		    if($(this).scrollTop() === 0) {
		        $(this).scrollTop(1);
		        fetchPreviousMessages();
		    }
    	});

	    $scope.$on(Pubnub.getMessageEventNameFor(vm.channel), function(ngEvent, m) {
	        $scope.$apply(function() {
	            vm.messages.push(m)
	        });
	        $scope.scrollDown(400);
	    });

	}

})();